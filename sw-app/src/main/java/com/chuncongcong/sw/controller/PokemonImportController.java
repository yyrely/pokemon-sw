package com.chuncongcong.sw.controller;

import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.importer.PokemonScaleMappingRunner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pokemon import")
@Slf4j
@RestController
@RequestMapping("/pokemon/import")
public class PokemonImportController {
    @Resource
    private PokemonScaleMappingRunner pokemonScaleMappingRunner;

    @Operation(summary = "Generate Scale World mappings")
    @PostMapping("/mapping")
    public ApiResponse<Void> generateMapping() {
        pokemonScaleMappingRunner.generateMappings();
        return ApiResponse.success();
    }
}
