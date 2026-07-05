package com.chuncongcong.sw.importer;

import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.PokemonAliasDO;
import com.chuncongcong.sw.entity.PokemonDO;
import com.chuncongcong.sw.entity.PokemonScaleMappingDO;
import com.chuncongcong.sw.entity.SubItemDO;
import com.chuncongcong.sw.service.ItemService;
import com.chuncongcong.sw.service.PokemonAliasService;
import com.chuncongcong.sw.service.PokemonScaleMappingService;
import com.chuncongcong.sw.service.PokemonService;
import com.chuncongcong.sw.service.SubItemService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class PokemonScaleMappingRunner {

    private static final String SOURCE_TYPE_ITEM = "item";

    private static final String SOURCE_TYPE_SUB_ITEM = "sub_item";

    @Resource
    private PokemonService pokemonService;
    @Resource
    private PokemonAliasService pokemonAliasService;
    @Resource
    private ItemService itemService;
    @Resource
    private SubItemService subItemService;
    @Resource
    private PokemonScaleMappingService pokemonScaleMappingService;

    public void generateMappings() {
        List<PokemonDO> pokemonList = pokemonService.list();
        List<ItemDO> itemList = itemService.listByRegionId(null);
        List<SubItemDO> subItemList = subItemService.listAll();
        Set<ItemPokemonKey> directItemMappings = new HashSet<>();
        Set<ItemPokemonKey> derivedItemMappings = new HashSet<>();
        for (PokemonDO pokemon : pokemonList) {
            List<String> aliases = aliasesOf(pokemon);
            for (String alias : aliases) {
                String normalizedAlias = normalize(alias);
                for (ItemDO item : itemList) {
                    if (match(pokemon.getId(), SOURCE_TYPE_ITEM, item.getId(), normalizedAlias, normalize(item.getName()),
                            "auto_name", new BigDecimal("100.00"), new BigDecimal("80.00"))) {
                        directItemMappings.add(new ItemPokemonKey(item.getId(), pokemon.getId()));
                    }
                }
                for (SubItemDO subItem : subItemList) {
                    if (match(pokemon.getId(), SOURCE_TYPE_SUB_ITEM, subItem.getId(), normalizedAlias, normalize(subItem.getName()),
                            "auto_name", new BigDecimal("100.00"), new BigDecimal("80.00"))) {
                        derivedItemMappings.add(new ItemPokemonKey(subItem.getItemId(), pokemon.getId()));
                    }
                }
            }
        }
        derivedItemMappings.removeAll(directItemMappings);
        fillItemMappingsFromSubItemMatches(derivedItemMappings, itemList);
    }

    private void fillItemMappingsFromSubItemMatches(Set<ItemPokemonKey> itemMappings, List<ItemDO> itemList) {
        Map<Long, ItemDO> itemMap = new HashMap<>();
        for (ItemDO item : itemList) {
            itemMap.put(item.getId(), item);
        }
        for (ItemPokemonKey key : itemMappings) {
            ItemDO item = itemMap.get(key.itemId());
            if (Objects.isNull(item)) {
                continue;
            }
            saveMapping(key.pokemonId(), SOURCE_TYPE_ITEM, key.itemId(), "auto_sub_item", new BigDecimal("90.00"), true);
        }
    }

    private List<String> aliasesOf(PokemonDO pokemon) {
        List<String> aliases = new ArrayList<>();
        aliases.add(pokemon.getNameZh());
        aliases.add(pokemon.getNameEn());
        aliases.add(pokemon.getApiPokemonName());
        aliases.addAll(pokemonAliasService.lambdaQuery()
                .eq(PokemonAliasDO::getPokemonId, pokemon.getId())
                .list()
                .stream()
                .map(PokemonAliasDO::getAlias)
                .toList());
        return aliases.stream().filter(Objects::nonNull).distinct().toList();
    }

    private boolean match(Long pokemonId, String sourceType, Long sourceId, String pokemonName, String sourceName,
                          String matchType, BigDecimal exactScore, BigDecimal containsScore) {
        if (pokemonName.isBlank() || sourceName.isBlank()) {
            return false;
        }
        boolean exact = sourceName.equals(pokemonName);
        boolean contains = sourceName.contains(pokemonName);
        if (!exact && !contains) {
            return false;
        }
        saveMapping(pokemonId, sourceType, sourceId, matchType, exact ? exactScore : containsScore, exact);
        return true;
    }

    private void saveMapping(Long pokemonId, String sourceType, Long sourceId, String matchType, BigDecimal matchScore,
                             boolean confirmed) {
        if (Objects.isNull(pokemonId) || Objects.isNull(sourceId)) {
            return;
        }
        PokemonScaleMappingDO mapping = pokemonScaleMappingService.lambdaQuery()
                .eq(PokemonScaleMappingDO::getPokemonId, pokemonId)
                .eq(PokemonScaleMappingDO::getSourceType, sourceType)
                .eq(PokemonScaleMappingDO::getSourceId, sourceId)
                .one();
        if (Objects.isNull(mapping)) {
            mapping = new PokemonScaleMappingDO();
        }
        mapping.setPokemonId(pokemonId);
        mapping.setSourceType(sourceType);
        mapping.setSourceId(sourceId);
        mapping.setMatchType(matchType);
        mapping.setMatchScore(matchScore);
        mapping.setConfirmed(confirmed);
        pokemonScaleMappingService.saveOrUpdate(mapping);
        log.info("Mapped pokemon {} to {} {}", pokemonId, sourceType, sourceId);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase()
                .replace(" ", "")
                .replace("　", "")
                .replace("-", "")
                .replace("・", "")
                .replace("（", "")
                .replace("）", "")
                .replace("(", "")
                .replace(")", "");
    }

    private record ItemPokemonKey(Long itemId, Long pokemonId) {
    }
}