-- -- 2.13.1
-- SELECT * from siteweb where url like 'http://https://%';
UPDATE siteweb SET url = right(url, -7) WHERE url LIKE 'http://https://%';
