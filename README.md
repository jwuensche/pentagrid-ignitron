<div style="text-align: center;">
    <h1>Welcome to pentagrid/ignitron 💡🔬</h1>
    <p>
        This is a research project, exploring the possibility of automatic detection of clones in test code, and suggestions and actions to fix this automated to a degree.
        The research you'll see here is still ongoing, the results will be published later on.
    </p>
</div>

## Description

This library utilizes javaparser and tree algorithms to perform static analysis on ASTs of testing code, to gain knowledge about code clones, and if
possible fix them automatically.
The library itself is written in kotlin using the partially functional approach that kotlin used.

## Structure

Probably most relevant for you is the `ignitron` directory as this contains the `ignitron` cli tool.
Instruction how to build and install can be found there.

> Unless specified otherwise all files are licensed under AGPL.
