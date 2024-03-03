create table if not exists test (
  a text,
  b int4,
  c bool
);

insert into test
values ('a', 1, true)
     , ('b', 2, false)
     , ('c', 3, true)
     , ('d', 4, false)
     , ('e', 5, true)
     , ('f', 6, false)
     , ('g', 7, true)
     , ('h', 8, false)
     , ('i', 9, true)
     , ('j', 10, false)
    on conflict do nothing;
