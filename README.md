## Description

This program provides desktop computer users with an easy-to-use graphical
interface to create/decode qrcode images using the renowned zxing barcode
library. It resembles the online
[QR Code Generator from the ZXing Project](http://zxing.appspot.com/generator/).
It can also decode qrcode from screen captures.

## Building

Before building qrcode-desktop, the following packages must be installed on
your system:

- jdk
- maven

Open a terminal and switch to the qrcode-desktop directory. Type the following
command to build the jar archive:

```
mvn package
```

If the build process is successful, the output jar file will be located in the
`target` directory. You can run the program by typing
`java -jar target/qrcode-desktop-*.jar`.
