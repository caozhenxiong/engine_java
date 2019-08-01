package com.alibabacloud.polar_race.engine.common.iolib;

import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;

public interface DirectIOLib extends Library {

    DirectIOLib INSTANCE = (DirectIOLib) Native.loadLibrary(Platform.isWindows() ? "msvcrt" : "c", DirectIOLib.class);


    int O_RDONLY = 00;
    int O_WRONLY = 01;
    int O_RDWR = 02;
    int O_CREAT = 0100;
    int O_TRUNC = 01000;
    int O_DIRECT = 040000;
    int O_SYNC = 04000000;


    NativeLong pwrite(int fd, Pointer buf, NativeLong count, NativeLong offset);

    NativeLong pread(int fd, Pointer buf, NativeLong count, NativeLong offset);

    int open(String pathname, int flags);

    int open(String pathname, int flags, int mode);

    int getpagesize();

    int pathconf(String path, int name);

    String strerror(int errnum);

    int close(int fd); // musn't forget to do this

    int posix_memalign(PointerByReference memptr, NativeLong alignment, NativeLong size);

}
