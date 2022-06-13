<div style="text-align: center;">
    <h1>Welcome to ignitron 🥽💡🔬</h1>
    <p>
        This is a research project, exploring the possibility of automatic detection of clones in test code, and suggestions and actions to fix this automated to a degree.
        The research you'll see here is still ongoing, the results will be published later on.
    </p>
</div>

## Description

This application utilized the `pentagrid` library developed in conjunction, implementing the internal logic for comparisons of Abstract Syntax Trees.
The ultimate goal is the automated detection of duplicated or copied sub trees in these, as well as the suggestion of modifications to minimize them.

> This project is still on-going, expect changes and instabilities on the way.

## Setup

For this repository you require gradle. A project build tool based on maven. All dependencies you need will be added once the build process begins.

A short description on how to install and setup gradle can be found [here](https://gradle.org/install/).

## Building and Testing

To build and run the tests:

``` sh
$ make
```

The process may take some time, but if everything is in order it should display a `Build successful`, all tests will also have passed at this point.

## Installing the Application

> Supported on linux and macos. If not standard on your distribution please add `$HOME/.local/bin` to your `$PATH`.

To install simply execute
```sh
$ make install
```
This installs the application to your local user `.local/bin` and `.local/lib` directories.

## Usage

```
$ ignitron -h
Usage: ignitron options_list
Options:
    --pullrequest, -p -> PR Branch (always required) { String }
    --basebranch, -b -> Base Branch { String }
    --applypatches, -a [false] -> Whether to apply patches automatically
    --help, -h -> Usage info
```

## Example

A small example repository for exploration can be found in the `example.tar`.
Contained are three commits based on a case found in javaparser. The archive also contains a README with more information.

``` sh
$ tar xf example.tar
$ cd example
$ ignitron -b 58ea852b42ac464faf61db514aecf01c8b4cccaa -p 5062e9d552e48f3f2bbe8a0ef14f73efc3277a15 | tail -n 1 | jq
```

It is advised to use `jq` to display the result for better human comprehension.

#### Author ✍️

<div>
  <a>Johannes Wünsche</a><br>
  <a href="https://spacesnek.rocks">📄 spacesnek.rocks</a>
</div>
