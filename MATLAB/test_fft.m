% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: May 2017
% 

format compact; format short; clear; close all; clc;

fs = 8000;              % Sampling frequency
Ts = 1/fs;
f0 = 200;               % Fundamental frequency
f1 = 500;
f2 = 2000;
N = 64;                 % FFT length
dt = (0:N-1) * Ts;


x = cos(2*pi*f1*dt) + cos(2*pi*f2*dt);
X = fft(x);
XdB = 20*log10(abs(X/N));
c = X/N;
f = (0:N-1)*(fs/N);

figure(1);
subplot(2,3,1), plot(dt,x), grid;
title('Signal f(t) = cos(2\pi \cdot 500 Hz \cdot t) + cos(2\pi \cdot 2 kHz \cdot t)');
xlabel('t [s] \rightarrow');
ylabel('x(t) \rightarrow');

subplot(2,3,2), stem(abs(c), 'filled'), grid;
title('fs = 8 kHz, 64-Punkt FFT, 125 Hz Bin');
xlabel('Bin-# m \rightarrow');
ylabel('|X[m]|/N \rightarrow');
xlim([0 N]);

subplot(2,3,3), plot(f, XdB, 'o'), grid;
title('FFT-Betragsspektrum (normiert)');
xlabel('f [Hz] \rightarrow');
ylabel('|X(f)|/N  [dB] \rightarrow');
axis([0 fs -100 0]);
ylim([-50 0]);


x = cos(2*pi*f0*dt);
X = fft(x);
XdB = 20*log10(abs(X/N));
c = X/N;
f = (0:N-1)*(fs/N);

subplot(2,3,4), plot(dt,x), grid;
title('Signal f(t) = cos(2\pi \cdot 200 Hz \cdot t)');
xlabel('t [s] \rightarrow');
ylabel('x(t) \rightarrow');

subplot(2,3,5), stem(abs(c), 'filled'), grid;
title('fs = 8 kHz, 64-Punkt FFT, 125 Hz Bin, ohne Fenster');
xlabel('Bin-# m \rightarrow');
ylabel('|X[m]|/N \rightarrow');
xlim([0 N]);

subplot(2,3,6), plot(f,XdB, 'o'), grid;
title('FFT-Betragsspektrum (normiert)');
xlabel('f [Hz] \rightarrow');
ylabel('|X(f)|/N  [dB] \rightarrow');
axis([0 fs -100 0]);
ylim([-50 0]);
