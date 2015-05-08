create function datePart
( Timestamp timestamp, datepart varchar(50) ) returns varchar(100)
parameter style java
no sql
language java
external name 'ch.eugster.colibri.derby.functions.Timestamp.datePart'
