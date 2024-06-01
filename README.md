# Mailer
Сервис отправки писем электронной почты. Данные берутся из файла формата MS Excel (должны быть столбцы email, subject (достаточно указать в одной строчке, text (достаточно указать в одной строчке)).
Столбец attachment - путь к файлу в формате pdf (заполняется автоматически файлами из папки atachments).

В папку attachments закидываем pdf файлы (приложения к письмам). Они будут автоматически добавлены в столбец attachment в файле data_for_sending.xlsx в алфавитном порядке
поэтому лучше эти файлы пронумировать.

В mail-config.txt вносим данные почтового сервера 
(для gmail нужно чтобы был пароль приложения, если его нет нужно сгенерировать в аккаунте google - обязательное условие
наличие двухфактороной авторизации иначе пароль приложений создать не получится.

В файл data_for_sending.xlsx в столбец email вносим список адресов электронной почты.
В столбец subject - указываем тему писем (достаточно указать в одной строчке, так как остальные письма будут брать текст темы из первой строки).
В столбец text - указываем текст письма (достаточно указать в одной строчке, так как остальные письма будут брать текст письма из первой строки).

Запускаем mailer.exe
