# DailyChest

![Paper](https://img.shields.io/badge/Paper-1.21.11-22c55e?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-f97316?style=for-the-badge&logo=openjdk)
![Version](https://img.shields.io/badge/version-1.0.1-111827?style=for-the-badge)
![License](https://img.shields.io/badge/license-MIT-2563eb?style=for-the-badge)

Ежедневная награда со streak и простым лутом.

## Версия

DailyChest 1.0.1

Paper 1.21.11  
API 1.21.11-R0.1-SNAPSHOT  
Java 21

## Команды

`/daily` - забрать ежедневную награду
`/daily reload` - перезагрузить конфиг
Алиас: `/dailychest`

## Permission

`dailychest.use`
`dailychest.reload`
Reload по умолчанию доступен op.

## Функции

- выдаёт награду раз в 24 часа;
- ведёт streak игрока;
- лут выбирается случайно;
- данные сохраняются в конфиг.

## Сборка

```bash
./gradlew build
```

Готовый `.jar` будет в `build/libs/`.
