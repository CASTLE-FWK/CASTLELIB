#!/bin/bash
#Chan11 IM plotting
sh plotChan.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Glider/chan11GoL25x25.tsv Glider 250 25x25;
sh plotChanHist.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Glider/chan11GoL25x25_histogram.tsv Glider 250 25x25;
sh plotChan.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/GliderGun/chan11GoL50x50.tsv GliderGun 250 50x50;
sh plotChanHist.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/GliderGun/chan11GoL50x50_histogram.tsv GliderGun 250 50x50;
sh plotChan.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Pulsar/chan11GoL25x25.tsv Pulsar 250 25x25;
sh plotChanHist.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Pulsar/chan11GoL25x25_histogram.tsv Pulsar 250 25x25;
sh plotChan.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/chan11GoL25x25.tsv Random 250 25x25;
sh plotChanHist.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/chan11GoL25x25_histogram.tsv Random 250 25x25;
sh plotChan.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/chan11GoL50x50.tsv Random 250 50x50;
sh plotChanHist.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/chan11GoL50x50_histogram.tsv Random 250 50x50;
sh plotChan.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/chan11GoL80x80.tsv Random 250 80x80;
sh plotChanHist.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/chan11GoL80x80_histogram.tsv Random 250 80x80;
sh plotChan.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/chan11GoL50x50-671.tsv Random 250 50x50-671;
sh plotChanHist.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/chan11GoL50x50-671_histogram.tsv Random 250 50x50-671;
sh plotChan.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/RPent/chan11GoL50x50.tsv RPent 250 50x50;
sh plotChanHist.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/RPent/chan11GoL50x50_histogram.tsv RPent 250 50x50;


#System Complexity Plotting
sh plotSystemComplexity.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Glider/systemComplexity25x25.tsv Glider 250 25x25;
sh plotSystemComplexity.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/GliderGun/systemComplexity50x50.tsv GliderGun 250 50x50;
sh plotSystemComplexity.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/RPent/systemComplexity50x50.tsv RPent 250 50x50;
sh plotSystemComplexity.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Pulsar/systemComplexity25x25.tsv Pulsar 250 25x25;
sh plotSystemComplexity.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/systemComplexity25x25.tsv Random 250 25x25;
sh plotSystemComplexity.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/systemComplexity50x50.tsv Random 250 50x50;
sh plotSystemComplexity.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/systemComplexity80x80.tsv Random 250 80x80;
sh plotSystemComplexity.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/systemComplexity50x50-671.tsv Random 250 50x50-671;

#OToole14 Plotting
sh plotOToole.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Glider/oToole1425x25.tsv Glider 250 25x25;
sh plotOToole.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/GliderGun/oToole1450x50.tsv GliderGun 250 50x50;
sh plotOToole.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/RPent/oToole1450x50.tsv RPent 250 50x50;
sh plotOToole.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Pulsar/oToole1425x25.tsv Pulsar 250 25x25;
sh plotOToole.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/oToole1425x25.tsv Random 250 25x25;
sh plotOToole.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/oToole1450x50.tsv Random 250 50x50;
sh plotOToole.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/oToole1480x80.tsv Random 250 80x80;
sh plotOToole.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/oToole1450x50-671.tsv Random 250 50x50-671;

#MSSE Plotting
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Glider/msse25x25_lifeQuad.tsv Glider 250 25x25 LifeQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Glider/msse25x25_interQuad.tsv Glider 250 25x25 InterQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/GliderGun/msse50x50_lifeQuad.tsv GliderGun 250 50x50 LifeQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/GliderGun/msse50x50_interQuad.tsv GliderGun 250 50x50 InterQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/RPent/msse50x50_lifeQuad.tsv RPent 250 50x50 LifeQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/RPent/msse50x50_interQuad.tsv RPent 250 50x50 InterQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Pulsar/msse25x25_lifeQuad.tsv Pulsar 250 25x25 LifeQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Pulsar/msse25x25_interQuad.tsv Pulsar 250 25x25 InterQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/msse25x25_lifeQuad.tsv Random 250 25x25 LifeQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/msse25x25_interQuad.tsv Random 250 25x25 InterQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/msse50x50_lifeQuad.tsv Random 250 50x50 LifeQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/msse50x50_interQuad.tsv Random 250 50x50 InterQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/msse80x80_lifeQuad.tsv Random 250 80x80 LifeQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/msse80x80_interQuad.tsv Random 250 80x80 InterQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/msse50x50-671_lifeQuad.tsv Random 250 50x50-671 LifeQuad;
sh plotMSSE.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/msse50x50-671_interQuad.tsv Random 250 50x50-671 InterQuad;

#BR Plotting
sh plotBR.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Glider/BR25x25.tsv Glider 250 25x25;
sh plotBR.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/GliderGun/BR50x50.tsv GliderGun 250 50x50;
sh plotBR.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/RPent/BR50x50.tsv RPent 250 50x50;
sh plotBR.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Pulsar/BR25x25.tsv Pulsar 250 25x25;
sh plotBR.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/BR25x25.tsv Random 250 25x25;
sh plotBR.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/BR50x50.tsv Random 250 50x50;
sh plotBR.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/BR80x80.tsv Random 250 80x80;
sh plotBR.sh /Users/lachlanbirdsey/repos/InterLib/observationModule/results/Random/BR50x50-671.tsv Random 250 50x50-671;