% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: May 2017
% 
% Audio effects tests

format compact; format short; clear; close all; clc;

NFFT = 8192;
window = hamming(NFFT+1);
[x, fs] = audioread('output/chirp_quad_up.wav');

figure('name', 'Bitcrusher');
subplot(1,2,1), spectrogram(x(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Signal only');

bits = 4;
normFreq = 0.5;
y = bitcrusher(x(:,1), bits, normFreq);
subplot(1,2,2), spectrogram(y, window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Bitcrusher: 4 bits, normalised frequency (f/fs) = 0.25');

figure('name', 'Ring modulation');
carrierFrequency = 500;
y = ringmod(x(:,1), fs, carrierFrequency);
spectrogram(y, window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Ring modulation');
