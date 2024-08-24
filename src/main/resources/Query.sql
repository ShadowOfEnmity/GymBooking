create table if not exists public.gym
(
    id          bigserial
        primary key,
    address     varchar(255),
    description text,
    latitude    varchar(10),
    longitude   varchar(11),
    name        varchar(255),
    phone       varchar(20),
    website     varchar(255)
);
create table if not exists public.gym_photo
(
    id bigserial  primary key,
    image_url varchar(255) not null,
    gym_id bigint not null references public.gym,
     alt  varchar(255)
);

create table if not exists public.users
(
    type           varchar(31)  not null,
    id             bigserial primary key,
    login          varchar(255) not null unique,
    password       varchar(255) not null,
    email          varchar(255),
    first_name     varchar(255) not null,
    last_name      varchar(255) not null,
    phone          varchar(20),
    role           varchar(25)  not null,
    availability   boolean,
    experience     text,
    rating         numeric(2, 1),
    specialization varchar(255),
    name           varchar(255)
);


create table if not exists public.booking
(
    id             bigserial primary key,
    booking_date   date,
    payment_status varchar(255),
    status         varchar(255),
    user_id        bigint not null references public.users
);

create table if not exists public.training_session
(
    id          bigserial primary key,
    capacity    integer,
    date        date,
    description text,
    duration    integer,
    price       numeric(10, 2),
    start_time  time,
    type        varchar(255),
    gym_id      bigint not null references public.gym,
    trainer_id  bigint not null references public.users
);

create table if not exists public.booking_training
(
    training_id bigint not null references public.training_session,
    booking_id  bigint not null references public.booking
);



INSERT INTO public.gym (address, description, latitude, longitude, name, phone, website)
VALUES ('ул. Ленина, 10', 'Современный фитнес-центр с бассейном', '55.7558', '37.6173', 'Фитнес-клуб "Спорт"',
        '+7(495)123-45-67', 'www.sportclub.ru');
INSERT INTO public.gym (address, description, latitude, longitude, name, phone, website)
VALUES ('пр. Мира, 5', 'Тренажерный зал с персональными тренерами', '55.7522', '37.6218', 'GymZone', '+7(499)987-65-43',
        'www.gymzone.ru');
INSERT INTO public.gym (address, description, latitude, longitude, name, phone, website)
VALUES ('ул. Пушкина, 15', 'Фитнес-клуб с групповыми занятиями', '55.7587', '37.6195', 'FitLife', '+7(495)555-44-33',
        'www.fitlife.ru');
INSERT INTO public.gym (address, description, latitude, longitude, name, phone, website)
VALUES ('пр. Победы, 20', 'Кроссфит-зал с опытными тренерами', '55.7532', '37.6229', 'CrossFit Power',
        '+7(499)888-77-66', 'www.crossfitpower.ru');

-- gym_photo
-- INSERT INTO public.gym_photo (image_url, gym_id, alt) VALUES
--     ('https://www.example.com/images/gym1.jpg', 1, 'Фото фитнес-центра "Спорт"');
-- INSERT INTO public.gym_photo (image_url, gym_id, alt) VALUES
--     ('https://www.example.com/images/gym2.jpg', 2, 'Фото тренажерного зала GymZone');
-- INSERT INTO public.gym_photo (image_url, gym_id, alt) VALUES
--     ('https://www.example.com/images/gym3.jpg', 3, 'Фото фитнес-клуба FitLife');
-- INSERT INTO public.gym_photo (image_url, gym_id, alt) VALUES
--     ('https://www.example.com/images/gym4.jpg', 4, 'Фото кроссфит-зала CrossFit Power');

-- users
INSERT INTO public.users (type, login, password, email, first_name, last_name, phone, role, availability, experience,
                          rating, specialization, name)
VALUES ('USER', 'user1', 'password1', 'user1@example.com', 'Иван', 'Иванов', '+7(910)123-45-67', 'USER', true,
        'Опыт работы 5 лет', 4.5, 'Силовые тренировки', 'Иван Иванов');
INSERT INTO public.users (type, login, password, email, first_name, last_name, phone, role, availability, experience,
                          rating, specialization, name)
VALUES ('TRAINER', 'trainer1', 'password2', 'trainer1@example.com', 'Петр', 'Петров', '+7(925)987-65-43', 'TRAINER',
        true, 'Опыт работы 10 лет', 4.8, 'Бодибилдинг', 'Петр Петров');
INSERT INTO public.users (type, login, password, email, first_name, last_name, phone, role, availability, experience,
                          rating, specialization, name)
VALUES ('USER', 'user2', 'password3', 'user2@example.com', 'Ольга', 'Смирнова', '+7(911)444-55-66', 'USER', true,
        'Новичок', 3.0, null, 'Ольга Смирнова');
INSERT INTO public.users (type, login, password, email, first_name, last_name, phone, role, availability, experience,
                          rating, specialization, name)
VALUES ('TRAINER', 'trainer2', 'password4', 'trainer2@example.com', 'Анна', 'Кузнецова', '+7(926)777-88-99', 'TRAINER',
        true, 'Опыт работы 7 лет', 4.2, 'Йога', 'Анна Кузнецова');

-- booking
INSERT INTO public.booking (booking_date, payment_status, status, user_id)
VALUES ('2024-03-01', 'PAID', 'CONFIRMED', 1);
INSERT INTO public.booking (booking_date, payment_status, status, user_id)
VALUES ('2024-03-08', 'UNPAID', 'WAITING', 1);
INSERT INTO public.booking (booking_date, payment_status, status, user_id)
VALUES ('2024-03-15', 'PAID', 'CONFIRMED', 3);
INSERT INTO public.booking (booking_date, payment_status, status, user_id)
VALUES ('2024-03-22', 'UNPAID', 'PENDING', 3);

-- training_session
INSERT INTO public.training_session (capacity, date, description, duration, price, start_time, type, gym_id, trainer_id)
VALUES (10, '2024-03-05', 'Силовая тренировка для начинающих', 60, 1000.00, '10:00:00', 'Силовая', 1, 2);
INSERT INTO public.training_session (capacity, date, description, duration, price, start_time, type, gym_id, trainer_id)
VALUES (8, '2024-03-12', 'Тренировка по бодибилдингу', 90, 1500.00, '18:00:00', 'Бодибилдинг', 2, 2);
INSERT INTO public.training_session (capacity, date, description, duration, price, start_time, type, gym_id, trainer_id)
VALUES (15, '2024-03-19', 'Йога для всех уровней', 45, 800.00, '17:00:00', 'Йога', 3, 4);
INSERT INTO public.training_session (capacity, date, description, duration, price, start_time, type, gym_id, trainer_id)
VALUES (12, '2024-03-26', 'Кроссфит тренировка', 60, 1200.00, '19:00:00', 'Кроссфит', 4, 2);

-- booking_training
INSERT INTO public.booking_training (training_id, booking_id)
VALUES (1, 1);
INSERT INTO public.booking_training (training_id, booking_id)
VALUES (2, 2);
INSERT INTO public.booking_training (training_id, booking_id)
VALUES (3, 3);
INSERT INTO public.booking_training (training_id, booking_id)
VALUES (4, 4);