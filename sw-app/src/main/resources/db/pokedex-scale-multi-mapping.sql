-- Allow one Scale World item/sub_item source to map to multiple Pokemon.
-- Run this after the original pokedex-mode.sql schema has already created the tables.

ALTER TABLE sw_pokemon_scale_mapping
    DROP INDEX uk_sw_pokemon_scale_mapping_source;

CREATE UNIQUE INDEX uk_sw_pokemon_scale_mapping_unique
    ON sw_pokemon_scale_mapping (pokemon_id, source_type, source_id);

CREATE INDEX idx_sw_pokemon_scale_mapping_source
    ON sw_pokemon_scale_mapping (source_type, source_id);

ALTER TABLE sw_user_pokemon_collect
    DROP INDEX uk_user_pokemon_scale_source;

CREATE UNIQUE INDEX uk_user_pokemon_scale_source
    ON sw_user_pokemon_collect (user_id, pokemon_id, source_type, source_collect_id, source_id);