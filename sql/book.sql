create table if not exists public.categories
(
    id          uuid      default uuid_generate_v4() not null
    constraint resources_pkey
    primary key,
    name        varchar(256)                         not null,
    slug        varchar(256)                         not null,
    description text,
    created_at  timestamp default CURRENT_TIMESTAMP,
    created_by  varchar(256)                         not null,
    updated_at  timestamp,
    updated_by  varchar(256),
    delete_flg  boolean   default false
    );

alter table public.categories
    owner to postgres;

create table if not exists public.books
(
    isbn             varchar(256)                                     not null
    constraint books_pk_2
    unique,
    title            varchar(1000)                                    not null,
    author           varchar(256)                                     not null,
    publication_year integer,
    publisher        varchar(256),
    image_url_s      text,
    "image_url-m"    text,
    "image-url-l"    text,
    available_copies integer      default 10,
    total_copies     integer      default 10,
    created_at       timestamp    default now()                       not null,
    created_by       varchar(256) default 'System'::character varying not null,
    updated_at       timestamp,
    updated_by       varchar(256),
    id               serial
    constraint books_pk
    primary key,
    category_id      uuid
    constraint books_categories_id_fk
    references public.categories,
    delete_flg       boolean      default false
    );

alter table public.books
    owner to postgres;

