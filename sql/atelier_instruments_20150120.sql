create table atelier_instruments (idaction integer, idpers integer, idinstru integer);
create unique index atelier_instruments_idx on atelier_instruments (idaction, idpers);
alter table atelier_instruments owner to nobody;