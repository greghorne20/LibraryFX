create table user (
  id integer primary key not null,
  name text unique not null collate nocase,
  email text not null collate nocase
)
