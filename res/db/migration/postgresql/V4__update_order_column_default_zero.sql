ALTER TABLE zone_intermediate_zones ALTER COLUMN zones_order SET DEFAULT 0;

UPDATE zone_intermediate_zones SET zones_order = 0 WHERE zones_order IS NULL;
