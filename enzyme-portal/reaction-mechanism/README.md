# reaction-mechanism

MCSA Rest client for the [Enzyme Portal](https://www.ebi.ac.uk/enzymeportal/) project.

## Built With

[Maven](https://maven.apache.org/)- Dependency Management


```bash
> mvn clean install
```

## Usage

Test use-cases to demonstrate how to use this module.
```java
import ReactionMechanismService

        String ec = "1.1.1.1";

        MechanismResult result = reactionMechanismService.findMechanismResultByEc(ec);

	String accession = "P00334";

        MechanismResult result = reactionMechanismService.findMechanismResultByAccession(accession);



```
## Authors

* [Joseph Sampson](https://www.linkedin.com/in/joseph-sampson-o-66399b30/)

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[Enzyme Portal](https://www.ebi.ac.uk/enzymeportal/) software released under the [Apache 2.0 license.](https://www.apache.org/licenses/LICENSE-2.0.html)
