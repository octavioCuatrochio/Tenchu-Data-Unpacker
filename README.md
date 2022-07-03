# Tenchu-Data-Unpacker
Source code and executable for unpacking the .VOL format from Tenchu (PS1).


## Overview

Started from the information left by [Stickman](https://forum.xentax.com/memberlist.php?mode=viewprofile&u=44424&sid=4df78ed9a389ba4006b958cff1d11895) over at the [forum](https://forum.xentax.com/viewtopic.php?t=11410) relating to this format in XenNTaX.

Written in Java, makes heavy use of the `java.nio` library.

Works by analizing the header and writing the bytes from the `.VOL` file table to the correspondent files directly.

## Format overview

The `.VOL` file is structured like this:

1. Header
2. File content (bytes for each file)
3. File table (name, size, etc for each file)

### Header


| Offset  | Value |
| ------------- |:-------------:|
|   1 - 12      | AFS_VOL_200     |
| 13 - 16      | Total number of files     |
| 17 - 20      | Offset to file table     |
| 21 - 40      | Zeroes |


### File table

Ubicated at the end of the `.VOL`, using the offset obtained from the header to reach it. 
Each individual file metadata is 32 bytes long, starts with `IX` and contains:

| Offset  | Value |
| ------------- |:-------------:|
|   1 - 2      | `IX` , signal of the start of the file   |
| 3 - 4      | Type (0x1 is a file, 0x2 is a directory)     |
| 5 - 8      | Offset to file contents (bytes, 0xcd if directory)     |
| 9 - 12     | File size (0xcd if directory) |
| 13 - 16    | File size again?? |
| 17 - 37    | File name, hierarchy, file format (see below) |

<br>

The file name works like this:
* Hierarchy in directory tree (@1, @2 to values like @390? i don't know why.)
<br> Eg: @1_IMAGES -> @2_FONTS.TIM then the file structure would be /IMAGES/FONTS.TIM

* Name in ASCII
* File format


### Executable usage

To make the `.Jar` work, you need to put the `.VOL` file in the same folder as the executable, and it needs to be named "DATA".



### Notes

Only tested with the `DATA.VOL` from SLUS_007 (Tenchu US Ver.)
