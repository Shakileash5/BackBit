![Build Status](https://img.shields.io/badge/build-completed-success?style=for-the-badge&logo=appveyor)
![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)

# BackBit

BackBit is a download manager developed using Java. The application is designed to be a simple and easy to use download manager, it downloads the files from the internet with n number of threads. 

## Installation

Clone the repo.

```bash
git clone https://github.com/Shakileash5/BackBit.git
```

## Usage

Run the application.

```bash
cd src/main/java
java Main.java
```

Enter the url of the file to download.
```
usage: [--url | --u] [--parts | --p]
--url   | --u: the url to be Downloaded
--parts | --p: the number of parts to split the download into
```

### Example

```bash
java Main.java --url=https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png --parts=8
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)