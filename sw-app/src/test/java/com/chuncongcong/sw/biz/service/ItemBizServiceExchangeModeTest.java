package com.chuncongcong.sw.biz.service;

import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.bean.param.ExchangeSearchParam;
import com.chuncongcong.sw.bean.vo.ExchangeItemVO;
import com.chuncongcong.sw.bean.vo.ExchangeUserCardVO;
import com.chuncongcong.sw.biz.service.impl.ItemBizServiceImpl;
import com.chuncongcong.sw.service.ItemInfoService;
import com.chuncongcong.sw.service.ItemService;
import com.chuncongcong.sw.service.SubItemService;
import com.chuncongcong.sw.service.UserItemCollectService;
import com.chuncongcong.sw.service.UserItemSummaryService;
import com.chuncongcong.sw.service.UserPokemonCollectService;
import com.chuncongcong.sw.service.UserService;
import com.chuncongcong.sw.service.UserSubItemCollectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemBizServiceExchangeModeTest {

    @Mock
    private ItemService itemService;
    @Mock
    private SubItemService subItemService;
    @Mock
    private ItemInfoService itemInfoService;
    @Mock
    private UserItemCollectService userItemCollectService;
    @Mock
    private UserSubItemCollectService userSubItemCollectService;
    @Mock
    private UserItemSummaryService userItemSummaryService;
    @Mock
    private UserService userService;
    @Mock
    private PokedexScaleSyncService pokedexScaleSyncService;
    @Mock
    private UserPokemonCollectService userPokemonCollectService;

    private ItemBizService itemBizService;

    @BeforeEach
    void setUp() {
        itemBizService = new ItemBizServiceImpl();
        ReflectionTestUtils.setField(itemBizService, "itemService", itemService);
        ReflectionTestUtils.setField(itemBizService, "subItemService", subItemService);
        ReflectionTestUtils.setField(itemBizService, "itemInfoService", itemInfoService);
        ReflectionTestUtils.setField(itemBizService, "userItemCollectService", userItemCollectService);
        ReflectionTestUtils.setField(itemBizService, "userSubItemCollectService", userSubItemCollectService);
        ReflectionTestUtils.setField(itemBizService, "userItemSummaryService", userItemSummaryService);
        ReflectionTestUtils.setField(itemBizService, "userService", userService);
        ReflectionTestUtils.setField(itemBizService, "pokedexScaleSyncService", pokedexScaleSyncService);
        ReflectionTestUtils.setField(itemBizService, "userPokemonCollectService", userPokemonCollectService);
        ContextHolder.setUserId(7L);
    }

    @AfterEach
    void tearDown() {
        ContextHolder.clear();
    }

    @Test
    void exchangeCardsShowScaleWorldExchangeUsersInPokedexMode() {
        ExchangeSearchParam param = new ExchangeSearchParam();
        param.setCollectMode("pokedex");
        ExchangeUserCardVO scaleCard = new ExchangeUserCardVO();
        scaleCard.setUserId(9L);
        scaleCard.setExchangeCount(1);
        ExchangeItemVO scalePreview = new ExchangeItemVO();
        scalePreview.setUserId(9L);
        scalePreview.setItemName("Scale World Box");
        when(userItemCollectService.listExchangeUsers(null, 0, 3, true)).thenReturn(List.of(scaleCard));
        when(userPokemonCollectService.listExchangeUsers(null, 0, 3, true)).thenReturn(List.of());
        when(userItemCollectService.listExchangeItemsByUserIds(List.of(9L), null)).thenReturn(List.of(scalePreview));
        when(userPokemonCollectService.listExchangeItemsByUserIds(List.of(9L), null)).thenReturn(List.of());

        List<ExchangeUserCardVO> result = itemBizService.exchangeCards(param);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(9L);
        assertThat(result.get(0).getItemList()).extracting(ExchangeItemVO::getItemName).containsExactly("Scale World Box");
    }

    @Test
    void exchangeUserItemsCombinesScaleWorldItemsBeforePokemonItems() {
        ExchangeSearchParam param = new ExchangeSearchParam();
        param.setUserId(9L);
        param.setPageNo(1);
        param.setPageSize(10);
        ExchangeItemVO scaleItem = new ExchangeItemVO();
        scaleItem.setItemName("Scale World Box");
        ExchangeItemVO pokemonItem = new ExchangeItemVO();
        pokemonItem.setItemName("Charmander");
        when(userItemCollectService.listExchangeItemsByUserId(9L, 0, 10)).thenReturn(List.of(scaleItem));
        when(userPokemonCollectService.listExchangeItemsByUserId(9L, 0, 10)).thenReturn(List.of(pokemonItem));

        List<ExchangeItemVO> result = itemBizService.exchangeUserItems(param);

        assertThat(result).extracting(ExchangeItemVO::getItemName)
                .containsExactly("Scale World Box", "Charmander");
    }

    @Test
    void myExchangeItemsUseOnlyPokemonCollectsInPokedexMode() {
        ExchangeSearchParam param = new ExchangeSearchParam();
        param.setCollectMode("pokedex");
        ExchangeItemVO pokemonItem = new ExchangeItemVO();
        pokemonItem.setItemName("Charmander");
        when(userPokemonCollectService.listMyExchangeItems(7L)).thenReturn(List.of(pokemonItem));

        List<ExchangeItemVO> result = itemBizService.myExchangeItems(param);

        assertThat(result).extracting(ExchangeItemVO::getItemName).containsExactly("Charmander");
    }

    @Test
    void myExchangeItemsUseScaleWorldCollectsByDefault() {
        ExchangeItemVO scaleItem = new ExchangeItemVO();
        scaleItem.setItemName("Scale World Box");
        when(userItemCollectService.listMyExchangeItems(7L)).thenReturn(List.of(scaleItem));

        List<ExchangeItemVO> result = itemBizService.myExchangeItems(null);

        assertThat(result).extracting(ExchangeItemVO::getItemName).containsExactly("Scale World Box");
    }
}