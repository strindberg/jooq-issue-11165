CREATE TABLE parent (
    id     bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name   text   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE child (
    name                 text         NOT NULL,
    parent_id         bigint,
    FOREIGN KEY (parent_id) REFERENCES parent (id)
);