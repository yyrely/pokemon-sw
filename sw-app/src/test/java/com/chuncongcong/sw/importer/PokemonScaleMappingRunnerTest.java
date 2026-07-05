package com.chuncongcong.sw.importer;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class PokemonScaleMappingRunnerTest {

    @Mock
    private PokemonService pokemonService;
    @Mock
    private PokemonAliasService pokemonAliasService;
    @Mock
    private ItemService itemService;
    @Mock
    private SubItemService subItemService;
    @Mock
    private PokemonScaleMappingService pokemonScaleMappingService;
    @Mock
    private LambdaQueryChainWrapper<PokemonAliasDO> aliasQuery;
    @Mock
    private LambdaQueryChainWrapper<PokemonScaleMappingDO> mappingQuery;

    private PokemonScaleMappingRunner runner;

    @BeforeEach
    void setUp() {
        runner = new PokemonScaleMappingRunner();
        ReflectionTestUtils.setField(runner, "pokemonService", pokemonService);
        ReflectionTestUtils.setField(runner, "pokemonAliasService", pokemonAliasService);
        ReflectionTestUtils.setField(runner, "itemService", itemService);
        ReflectionTestUtils.setField(runner, "subItemService", subItemService);
        ReflectionTestUtils.setField(runner, "pokemonScaleMappingService", pokemonScaleMappingService);

        when(pokemonAliasService.lambdaQuery()).thenReturn(aliasQuery);
        when(aliasQuery.eq(any(), any())).thenReturn((LambdaQueryChainWrapper) aliasQuery);
        when(aliasQuery.list()).thenReturn(List.of());

        when(pokemonScaleMappingService.lambdaQuery()).thenReturn(mappingQuery);
        when(mappingQuery.eq(any(), any())).thenReturn((LambdaQueryChainWrapper) mappingQuery);
        when(mappingQuery.one()).thenReturn(null);
    }

    @Test
    void generateMappingsMapsParentItemWhenSubItemMatchesPokemonName() {
        PokemonDO pokemon = new PokemonDO();
        pokemon.setId(4L);
        pokemon.setNameZh("小火龙");

        ItemDO item = new ItemDO();
        item.setId(101L);
        item.setName("关都地区通贩1");

        SubItemDO subItem = new SubItemDO();
        subItem.setId(201L);
        subItem.setItemId(101L);
        subItem.setName("小火龙&杰尼龟&妙蛙种子");

        when(pokemonService.list()).thenReturn(List.of(pokemon));
        when(itemService.listByRegionId(null)).thenReturn(List.of(item));
        when(subItemService.listAll()).thenReturn(List.of(subItem));

        runner.generateMappings();

        ArgumentCaptor<PokemonScaleMappingDO> mappingCaptor = ArgumentCaptor.forClass(PokemonScaleMappingDO.class);
        verify(pokemonScaleMappingService, times(2)).saveOrUpdate(mappingCaptor.capture());

        assertThat(mappingCaptor.getAllValues())
                .extracting(PokemonScaleMappingDO::getPokemonId, PokemonScaleMappingDO::getSourceType,
                        PokemonScaleMappingDO::getSourceId)
                .containsExactlyInAnyOrder(
                        tuple(4L, "sub_item", 201L),
                        tuple(4L, "item", 101L)
                );
    }
}
