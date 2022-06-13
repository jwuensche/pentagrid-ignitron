<div style="text-align: center;">
    <h1>Welcome to pentagrid 💡🔬</h1>
    <p>
        This is a research project, exploring the possibility of automatic detection of clones in test code, and suggestions and actions to fix this automated to a degree.
        The research you'll see here is still ongoing, the results will be published later on.
    </p>
</div>

## Description

This library utilizes javaparser and tree algorithms to perform static analysis on ASTs of testing code, to gain knowledge about code clones, and if
possible fix them automatically.
The library itself is written in kotlin using the partially functional approach that kotlin used.

## Setup

For this repository you require gradle. A project build tool based on maven. All dependencies you need will be added once the build process begins.

A short description on how to install and setup gradle can be found [here](https://gradle.org/install/).

## Building and Testing

To build and run the tests:

``` sh
$ ./gradlew build
```

The process may take some time, but if everything is in order it should display a `Build successful` in the end, this indicates that the project could be build and all tests succeeded.


#### Author ✍️

<div>
  <a>🤺 Johannes Wünsche</a><br>
  <a href="https://spacesnek.rocks">📄 spacesnek.rocks</a>
</div>
