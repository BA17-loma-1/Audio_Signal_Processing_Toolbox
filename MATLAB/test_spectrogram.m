% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: May 2017
% 
% Test

format compact; format short; clear; close all; clc;

NFFT = 8192;
window = hamming(NFFT+1);
b_fir_lowpass = fir_lowpass();
b_fir_highpass = fir_highpass();


figure('name', 'Chirps');
%[x, fs] = audioread('output/chirp_quad_up.wav');
%subplot(1,2,1), spectrogram(x(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
%title('Quadratic concave upsweep');

[x, fs] = audioread('output/chirp_quad_convex.wav');
subplot(1,2,1), spectrogram(x(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Symmetric convex quadratic chirp');

[x, fs] = audioread('output/cosine.wav');
subplot(1,2,2), spectrogram(x(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Cosine');


figure('name', 'sinusoidal signals');
[x, fs] = audioread('output/cosine.wav');
subplot(1,3,1), spectrogram(x(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Cosine');

[x, fs] = audioread('output/sawtooth.wav');
subplot(1,3,2), spectrogram(x(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Sawtooth');

[x, fs] = audioread('output/square.wav');
subplot(1,3,3), spectrogram(x(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Square');


figure('name', 'Filtered signals');
[x, fs] = audioread('output/chirp_quad_up.wav');
y = filter(b_fir_lowpass, 1, x);
subplot(1,2,1); spectrogram(y(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Upsweep low pass filtered');

[x, fs] = audioread('output/cosine.wav');
y = filter(b_fir_highpass, 1, x);
subplot(1,2,2); spectrogram(y(:,1), window, 0, NFFT, fs, 'yaxis'), grid minor;
title('Cosine high pass filtered');
