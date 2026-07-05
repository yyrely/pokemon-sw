package com.chuncongcong.sw.biz.service;

import com.chuncongcong.sw.bean.vo.PokedexScaleSyncResultVO;
import com.chuncongcong.sw.entity.PokemonScaleMappingDO;
import com.chuncongcong.sw.entity.UserDO;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import com.chuncongcong.sw.entity.UserPokemonCollectDO;
import com.chuncongcong.sw.entity.UserSubItemCollectDO;
import com.chuncongcong.sw.service.PokemonScaleMappingService;
import com.chuncongcong.sw.service.UserItemCollectService;
import com.chuncongcong.sw.service.UserPokemonCollectService;
import com.chuncongcong.sw.service.UserService;
import com.chuncongcong.sw.service.UserSubItemCollectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PokedexScaleSyncServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private UserItemCollectService userItemCollectService;
    @Mock
    private UserSubItemCollectService userSubItemCollectService;
    @Mock
    private UserPokemonCollectService userPokemonCollectService;
    @Mock
    private PokemonScaleMappingService pokemonScaleMappingService;

    private PokedexScaleSyncService pokedexScaleSyncService;

    @BeforeEach
    void setUp() {
        pokedexScaleSyncService = new com.chuncongcong.sw.biz.service.impl.PokedexScaleSyncServiceImpl(
                userService,
                userItemCollectService,
                userSubItemCollectService,
                userPokemonCollectService,
                pokemonScaleMappingService
        );
    }

    @Test
    void rebuildUserScaleCollectsCopiesItemAndSubItemRecordsThroughUnconfirmedMappings() {
        Long userId = 7L;
        UserDO user = new UserDO();
        user.setId(userId);
        when(userService.getById(userId)).thenReturn(user);

        UserItemCollectDO itemCollect = itemCollect(101L, userId, 1001L, "item remark");
        UserSubItemCollectDO subItemCollect = subItemCollect(201L, userId, 1002L, "[3001,3002]", "sub remark");
        when(userItemCollectService.listByUserId(userId)).thenReturn(List.of(itemCollect));
        when(userSubItemCollectService.listByUserId(userId)).thenReturn(List.of(subItemCollect));
        when(pokemonScaleMappingService.listBySource("item", List.of(1001L)))
                .thenReturn(List.of(mapping(9001L, "item", 1001L, false)));
        when(pokemonScaleMappingService.listBySource("sub_item", List.of(3001L, 3002L)))
                .thenReturn(List.of(mapping(9002L, "sub_item", 3001L, false)));

        PokedexScaleSyncResultVO result = pokedexScaleSyncService.syncUserScaleCollects(userId);

        verify(userPokemonCollectService).removeScaleWorldSourcesByUserId(userId);
        ArgumentCaptor<List> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(userPokemonCollectService).saveBatch(saveCaptor.capture());
        List<UserPokemonCollectDO> saved = saveCaptor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved).extracting(UserPokemonCollectDO::getPokemonId).containsExactlyInAnyOrder(9001L, 9002L);
        assertThat(saved).extracting(UserPokemonCollectDO::getCollectibleTypeCode).containsOnly("scale_world");
        assertThat(saved).extracting(UserPokemonCollectDO::getSourceType).containsExactlyInAnyOrder("scale_item", "scale_sub_item");
        assertThat(saved).extracting(UserPokemonCollectDO::getSourceId).containsExactlyInAnyOrder(1001L, 3001L);
        assertThat(saved).extracting(UserPokemonCollectDO::getExchange).containsOnlyNulls();
        assertThat(saved).extracting(UserPokemonCollectDO::getExchangeRemark).containsOnlyNulls();
        assertThat(result.getInsertCount()).isEqualTo(2);
        assertThat(result.getSkipNoMappingCount()).isEqualTo(1);
        assertThat(result.getSyncStatus()).isEqualTo(1);
        verify(userService).updateById(any(UserDO.class));
    }

    @Test
    void syncItemCollectReplacesSourceRecordsBeforeSavingCurrentDerivedRecord() {
        UserItemCollectDO itemCollect = itemCollect(101L, 7L, 1001L, "updated remark");
        when(pokemonScaleMappingService.listBySource("item", List.of(1001L)))
                .thenReturn(List.of(mapping(9001L, "item", 1001L, true)));

        pokedexScaleSyncService.syncItemCollect(itemCollect);

        verify(userPokemonCollectService).removeScaleWorldSource(7L, "scale_item", 101L);
        ArgumentCaptor<List> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(userPokemonCollectService).saveBatch(saveCaptor.capture());
        UserPokemonCollectDO saved = (UserPokemonCollectDO) saveCaptor.getValue().get(0);
        assertThat(saved.getUserId()).isEqualTo(7L);
        assertThat(saved.getPokemonId()).isEqualTo(9001L);
        assertThat(saved.getRemark()).isEqualTo("updated remark");
        assertThat(saved.getSourceCollectId()).isEqualTo(101L);
    }

    @Test
    void syncItemCollectSavesAllPokemonMappingsForOneItem() {
        UserItemCollectDO itemCollect = itemCollect(101L, 7L, 1001L, "kanto retail set");
        when(pokemonScaleMappingService.listBySource("item", List.of(1001L)))
                .thenReturn(List.of(
                        mapping(4L, "item", 1001L, true),
                        mapping(7L, "item", 1001L, true)
                ));

        pokedexScaleSyncService.syncItemCollect(itemCollect);

        ArgumentCaptor<List> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(userPokemonCollectService).saveBatch(saveCaptor.capture());
        List<UserPokemonCollectDO> saved = saveCaptor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved).extracting(UserPokemonCollectDO::getPokemonId).containsExactlyInAnyOrder(4L, 7L);
        assertThat(saved).extracting(UserPokemonCollectDO::getSourceType).containsOnly("scale_item");
        assertThat(saved).extracting(UserPokemonCollectDO::getSourceId).containsOnly(1001L);
    }

    @Test
    void syncSubItemCollectSkipsSaveWhenNoParsedSubItemsHaveMapping() {
        UserSubItemCollectDO subItemCollect = subItemCollect(201L, 7L, 1002L, "[3001]", "sub remark");
        when(pokemonScaleMappingService.listBySource("sub_item", List.of(3001L))).thenReturn(List.of());

        pokedexScaleSyncService.syncSubItemCollect(subItemCollect);

        verify(userPokemonCollectService).removeScaleWorldSource(7L, "scale_sub_item", 201L);
        verify(userPokemonCollectService, never()).saveBatch(any());
    }

    private UserItemCollectDO itemCollect(Long id, Long userId, Long itemId, String remark) {
        UserItemCollectDO record = new UserItemCollectDO();
        record.setId(id);
        record.setUserId(userId);
        record.setItemId(itemId);
        record.setCollectDate(LocalDate.of(2026, 7, 5));
        record.setCollectPrice(new BigDecimal("12.34"));
        record.setRemark(remark);
        record.setExchange(true);
        record.setExchangeRemark("exchange remark");
        return record;
    }

    private UserSubItemCollectDO subItemCollect(Long id, Long userId, Long itemId, String subItemId, String remark) {
        UserSubItemCollectDO record = new UserSubItemCollectDO();
        record.setId(id);
        record.setUserId(userId);
        record.setItemId(itemId);
        record.setSubItemId(subItemId);
        record.setCollectDate(LocalDate.of(2026, 7, 5));
        record.setCollectPrice(new BigDecimal("23.45"));
        record.setRemark(remark);
        record.setExchange(false);
        return record;
    }

    private PokemonScaleMappingDO mapping(Long pokemonId, String sourceType, Long sourceId, Boolean confirmed) {
        PokemonScaleMappingDO mapping = new PokemonScaleMappingDO();
        mapping.setPokemonId(pokemonId);
        mapping.setSourceType(sourceType);
        mapping.setSourceId(sourceId);
        mapping.setConfirmed(confirmed);
        return mapping;
    }
}
