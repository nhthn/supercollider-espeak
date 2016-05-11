SuperCollider tool for extracting data from the eSpeak phoneme database. I wrote it so I can steal formant data for designing vowel filters.

Don't get your hopes up, this does not actually do synthesis or call eSpeak.

    // obtain a copy of eSpeak source code
    x = Espeak("/path/to/espeak/");
    x.db['vowel/a'].frames[0].freqs.postln;