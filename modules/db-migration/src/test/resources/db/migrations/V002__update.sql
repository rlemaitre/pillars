alter table if exists test add column d text null ;

update test set d = 'd' where c = true;