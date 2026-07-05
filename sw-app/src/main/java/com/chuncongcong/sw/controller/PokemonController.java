package com.chuncongcong.sw.controller;

import com.chuncongcong.framework.interceptor.IgnoreLogin;
import com.chuncongcong.framework.response.ApiListResponse;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.bean.param.PokemonParam;
import com.chuncongcong.sw.bean.param.UserPokemonCollectParam;
import com.chuncongcong.sw.bean.vo.PokemonStatsVO;
import com.chuncongcong.sw.bean.vo.PokemonTypeVO;
import com.chuncongcong.sw.bean.vo.PokemonVO;
import com.chuncongcong.sw.bean.vo.PokedexScaleSyncResultVO;
import com.chuncongcong.sw.bean.vo.UserPokemonCollectVO;
import com.chuncongcong.sw.biz.service.PokedexScaleSyncService;
import com.chuncongcong.sw.biz.service.PokemonBizService;
import com.chuncongcong.sw.entity.CollectibleTypeDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pokemon Pokedex")
@Slf4j
@RestController
@RequestMapping("/pokemon")
public class PokemonController {

    @Resource
    private PokemonBizService pokemonBizService;
    @Resource
    private PokedexScaleSyncService pokedexScaleSyncService;

    @IgnoreLogin
    @Operation(summary = "Pokemon list")
    @PostMapping("/list")
    public ApiListResponse<PokemonVO> list(@RequestBody PokemonParam param) {
        return ApiListResponse.success(pokemonBizService.list(param));
    }

    @IgnoreLogin
    @Operation(summary = "Pokemon info")
    @PostMapping("/info")
    public ApiResponse<PokemonVO> info(@RequestBody PokemonParam param) {
        return ApiResponse.success(pokemonBizService.info(param));
    }

    @Operation(summary = "Pokemon collect log list")
    @PostMapping("/collect/log/list")
    public ApiListResponse<UserPokemonCollectVO> collectList(@RequestBody UserPokemonCollectParam param) {
        return ApiListResponse.success(pokemonBizService.userCollectList(param));
    }

    @Operation(summary = "Pokemon collect save or update")
    @PostMapping("/collect/log/saveOrUpdate")
    public ApiResponse<Void> saveOrUpdate(@RequestBody UserPokemonCollectParam param) {
        pokemonBizService.userSaveOrUpdate(param);
        return ApiResponse.success();
    }

    @Operation(summary = "Pokemon collect delete")
    @PostMapping("/collect/log/delete")
    public ApiResponse<Void> delete(@RequestBody UserPokemonCollectParam param) {
        pokemonBizService.userDelete(param);
        return ApiResponse.success();
    }

    @Operation(summary = "Toggle Pokemon collect exchange status")
    @PostMapping("/collect/log/exchange")
    public ApiResponse<Void> exchange(@RequestBody UserPokemonCollectParam param) {
        pokemonBizService.userExchange(param);
        return ApiResponse.success();
    }

    @Operation(summary = "Pokemon region stats")
    @PostMapping("/stats/region")
    public ApiResponse<PokemonStatsVO> stats(@RequestBody PokemonParam param) {
        return ApiResponse.success(pokemonBizService.stats(param));
    }

    @IgnoreLogin
    @Operation(summary = "Pokemon collectible type list")
    @PostMapping("/collectible-type/list")
    public ApiListResponse<CollectibleTypeDO> collectibleTypes() {
        return ApiListResponse.success(pokemonBizService.collectibleTypes());
    }

    @IgnoreLogin
    @Operation(summary = "Pokemon type list")
    @PostMapping("/type/list")
    public ApiListResponse<PokemonTypeVO> pokemonTypes() {
        return ApiListResponse.success(pokemonBizService.pokemonTypes());
    }

    @Operation(summary = "Sync Scale World collects to Pokedex by user id")
    @IgnoreLogin
    @PostMapping("/collect/sync/scale-world")
    public ApiResponse<PokedexScaleSyncResultVO> syncScaleWorld(@RequestBody PokemonParam param) {
        return ApiResponse.success(pokedexScaleSyncService.syncUserScaleCollects(param.getUserId()));
    }
}
