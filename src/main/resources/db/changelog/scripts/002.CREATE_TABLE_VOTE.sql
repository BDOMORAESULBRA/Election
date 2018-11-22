create table vote (
  id integer identity primary key,
  election_id integer not null,
  voter_id integer not null,
  number_election integer,
  blank_vote boolean not null,
  null_vote boolean not null
);
