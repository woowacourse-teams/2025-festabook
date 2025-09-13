ALTER TABLE place
    MODIFY category ENUM(
    'BOOTH',
    'BAR',
    'FOOD_TRUCK',
    'STAGE',
    'PHOTO_BOOTH',
    'SMOKING',
    'TRASH_CAN',
    'TOILET',
    'PARKING',
    'PRIMARY',
    'EXTRA'
    ) NOT NULL;
