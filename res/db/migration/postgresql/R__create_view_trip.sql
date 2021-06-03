CREATE VIEW v_trip AS
SELECT c.id                  as id,
       z_from.id             as from_id,
       z_from.name           as from_name,
       z_from.authority_name as from_authority,
       z_to.id               as to_id,
       z_to.name             as to_name,
       z_to.authority_name   as to_authority
FROM zone_connection c
         LEFT JOIN zone z_from on z_from.id = SPLIT_PART(c.id, '-', 1)
         LEFT JOIN zone z_to on z_to.id = SPLIT_PART(c.id, '-', 2);