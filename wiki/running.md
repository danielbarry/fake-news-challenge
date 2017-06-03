# Running

To run the code, here is an example command:

    java -jar fnc.jar -c fnc-1/train_stances.formatted.csv \
    fnc-1/train_bodies.formatted.csv -u \
    fnc-1/train_stances.random.formatted.csv

**NOTE:** The "formatted" data is with commas removed from the cells. This is
because the `String` `.split()` method assumes this is a basic CSV file and
doesn't implement the full ISO standard. The newlines are also removed from the
inside of the cells for the same reason. The data is formatted using
`libreoffice`'s `calc`, where `\n` is replaced with ` `, as well as `,` being
replaced with ` `.
