-- 2.9.4.13
CREATE TABLE module_preset(
  id serial,
  nom varchar(128),
  modules int[]
);