# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table artworks (
  artid                     bigint auto_increment not null,
  uid                       bigint,
  file_path                 varchar(255),
  title                     varchar(255),
  votes                     integer,
  voted_on                  integer,
  aucId                     bigint,
  constraint uq_artworks_aucId unique (aucId),
  constraint pk_artworks primary key (artid))
;

create table auctions (
  auc_id                    bigint auto_increment not null,
  artid                     bigint,
  open_date                 varchar(255),
  close_date                varchar(255),
  bid_count                 bigint,
  current_bid               bigint,
  have_high_bid             varchar(255),
  artwork_artid             bigint,
  constraint uq_auctions_artwork_artid unique (artwork_artid),
  constraint pk_auctions primary key (auc_id))
;

create table comments (
  comment_id                bigint auto_increment not null,
  uid                       bigint,
  artid                     bigint,
  content                   varchar(255),
  constraint pk_comments primary key (comment_id))
;

create table users (
  uid                       bigint auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  email                     varchar(255),
  votes                     integer,
  constraint pk_users primary key (uid))
;

alter table artworks add constraint fk_artworks_user_1 foreign key (uid) references users (uid) on delete restrict on update restrict;
create index ix_artworks_user_1 on artworks (uid);
alter table artworks add constraint fk_artworks_auction_2 foreign key (aucId) references auctions (auc_id) on delete restrict on update restrict;
create index ix_artworks_auction_2 on artworks (aucId);
alter table auctions add constraint fk_auctions_artwork_3 foreign key (artwork_artid) references artworks (artid) on delete restrict on update restrict;
create index ix_auctions_artwork_3 on auctions (artwork_artid);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table artworks;

drop table auctions;

drop table comments;

drop table users;

SET FOREIGN_KEY_CHECKS=1;

