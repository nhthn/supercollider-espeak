Espeak {
    var espeakPath, phonPath, manifestPath;
    var phonFile, <db;

    *new { |espeakPath|
        ^super.new.init(espeakPath);
    }

    init { |argEspeakPath|
        var mstring;
        espeakPath = argEspeakPath;
        phonPath = espeakPath +/+ "espeak-data/phondata";
        manifestPath = espeakPath +/+ "espeak-data/phondata-manifest";

        phonFile = File(phonPath, "rb");
        File.use(manifestPath, "r", { |f|
            mstring = f.readAllString;
        });
        db = Dictionary();
        mstring.split($\n).do({ |line|
            ((line.size > 0) and: { line[0] != $# }).if {
                var name, pos, code;
                line = line.replace("  ", " ").split($ );
                name = line[2].asSymbol;
                pos = line[1].asFloat.asInteger;
                code = line[0].asSymbol;
                phonFile.seek(pos);
                (code == \S).if {
                    db[name] = this.readSpectrum;
                };
            };
        });
    }

    readUChar {
        ^phonFile.getInt8.mod(256);
    }

    readShort {
        ^phonFile.getInt16LE;
    }

    readSpectrum {
        var f = phonFile;
        var spect = EspeakSpectrum();
        spect.totalLength = this.readShort;
        spect.numFrames = this.readUChar;
        spect.flags = this.readUChar;
        spect.frames = spect.numFrames.collect {
            this.readFrame;
        };
        ^spect;
    }

    readFrame {
        var f = phonFile;
        var frame = EspeakFrame();
        frame.flags = this.readShort;
        frame.readFlags;
        frame.freqs = 7.collect { this.readShort };
        frame.length = this.readUChar;
        frame.rms = this.readUChar;
        frame.heights = 8.collect { this.readUChar };
        frame.widths = 6.collect { this.readUChar };
        frame.rights = 3.collect { this.readUChar };
        frame.bws = 4.collect { this.readUChar };
        frame.klatt1 = 5.collect { this.readUChar };
        frame.klatt.if {
            frame.klatt2 = 5.collect { this.readUChar };
            frame.klattAmps = 7.collect { this.readUChar };
            frame.klattBws = 7.collect { this.readUChar };
            this.readUChar;
        };
        ^frame;
    }

    free {
        phonFile.close;
    }

}

EspeakSpectrum {
    var <>totalLength;
    var <>numFrames;
    var <>flags;
    var <>frames;
}

EspeakFrame {
    var <>flags;
    var <>freqs;
    var <>length;
    var <>rms;
    var <>heights;
    var <>widths;
    var <>rights;
    var <>bws;
    var <>klatt1;
    var <>klatt2;
    var <>klattAmps;
    var <>klattBws;

    var <>klatt;
    readFlags {
        klatt = (flags & 0x01) != 0;
    }
}