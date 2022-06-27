# nrich-bom

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-bom/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-bom)

## Overview

Contains nrich project dependency versions and is published as BOM (Bill Of Materials). With this module users can avoid specifying each module version manually and be sure they have compatible
versions of all the libraries. On top of all nrich module versions it contains Apache POI and ModelMapper versions since they are used inside nrich (Spring Boot's dependency versions are not exported
and users should add them manually).
