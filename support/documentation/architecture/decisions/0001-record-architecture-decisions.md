# Record architecture decisions

- Status: accepted
- Decision makers: agrancaric, jsajlovic, jzrilic, mfolnovic
- Date: 2022-03-02

## Context and Problem Statement

We want an easy way of recording our architecture and coding choices. Which template fits our development style best and is not too big that it will discourage the team to write templates?

## Considered Options

- [MADR template](https://adr.github.io/madr/)
- [Michael Nygard's template](https://github.com/joelparkerhenderson/architecture-decision-record/blob/main/templates/decision-record-template-by-michael-nygard/index.md)
- Other templates from https://github.com/joelparkerhenderson/architecture-decision-record
- Custom template

## Decision Outcome

Chosen custom template based on MADR template. MADR template seemed to fit our documentation needs the best, but we weren't satisfied with some (minor) aspects of MADR template. We decided to change
those parts and add the template to our repository for possible further changes. We also decided to record the previous decisions to explain the current structure of the project.

## Links

- [Documenting Architecture Decisions - Michael Nygard](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
- [Markdown Architectural Decision Records](https://adr.github.io/madr/)
- [When Should I Write an Architecture Decision Record - Spotify](https://engineering.atspotify.com/2020/04/when-should-i-write-an-architecture-decision-record/)
- [ADRs - Joel Parker Henderson](https://github.com/joelparkerhenderson/architecture-decision-record)
