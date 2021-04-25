# --- !Ups

INSERT INTO "category"("name") VALUES("sample1");
INSERT INTO "category"("name") VALUES("sample2");

# --- !Downs

DELETE FROM "category" WHERE name="sample1"";
DELETE FROM "category" WHERE name="sample2"";
