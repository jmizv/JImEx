# JImEx

This is a fork of [JIExplorer](https://sourceforge.net/projects/jiexplorer) (Java Image Viewer/Explorer).

Original description from the SourceForge project page:

> JIExplorer is a java image viewer / explorer desk top application modeled after ACDSee image viewer. Features include:
> thumbnails, preview panel, key word, category, and date image management, image zooming, full screen, slide shows. Supported image form.

This repository is the attempt to bring this application to the year 2023 and to Java version 17 with which you can
use `var` and multi-catch statements as well as other cool stuff.

My personal motivation is next to working with an established but out-dated application, having a workable image
tagging software. That means I really want to work with it. Also, there was
[a request](https://softwarerecs.stackexchange.com/questions/594/an-open-source-java-picture-viewing-tagging-program)
on [Software Recommendations](https://softwarerecs.stackexchange.com).
So, if you are not one of these people that put their private photographs on InstXgrXm or Fxcxbxxk and want to manage
them offline, this is for you.

# Already done ✅

Some things are already done. Technically you should be able to check out the code and run the application in your
IDE. Simply execute `de.jmizv.jiexplorer.Splasher`.

- switching from build tool Ant to Gradle 🚫🐜
- updating some libraries 📗📘📙
- migrating small portions of code for reading images and their metadata just to make it work in Java 17

# Things to do 🎫

A lot of stuff has to be done. Maybe not everything will be done. For example, one person alone might be not able to
test this actual cross-platform software on more than one system. Also, I don't have all the different image formats.
Consider this a spare-time project. Help is really appreciated.

- improving the DB architecture (I'd like to have PostgreSQL support, too :3 but this is quite extra work)
- ???
- is the `com.adobe.internal.xmp` package still needed?
- where are the unit and integration test 🤔
- Creating some documentation