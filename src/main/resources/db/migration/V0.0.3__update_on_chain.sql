alter table vote_reward_process add column description varchar(255);

alter table vote_reward_process add column message varchar(255);

alter table vote_reward_process add column airdrop_json LongText;

alter table vote_reward_process add column chain_id integer;

alter table vote_reward_process add column name varchar(100);
-- alter table vote_reward_process drop index UniqueName;
alter table vote_reward_process add constraint UniqueName unique (name);

alter table vote_reward_process add column on_chain_transaction_hash varchar(66) not null;

alter table vote_reward_process add column revoke_on_chain_transaction_hash varchar(66) not null;

-- update airdrop_records schema --
-- alter table airdrop_records drop index UniqueAirdropAddress;
alter table airdrop_records add constraint UniqueAirdropAddress unique (airdrop_id, address);

