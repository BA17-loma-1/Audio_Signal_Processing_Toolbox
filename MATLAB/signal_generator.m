% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: February 2017
% 
% Audio signal generation with MATLAB

format compact; format shortE; clear; clc;

fs = 48e3;              % Sampling frequency
T = 10;                 % Signal duration
t = 0:1/fs:T;
f0 = 500;               % Fundamental frequency
t0 = 1/f0;              % Signal period
w = 2*pi*f0;            % Omega (angular frequency)
A = 0.25;                % Amplitude

% Generate signals
x_sine = A * sin(w*t);
x_sawtooth = A * sawtooth(w*t);
x_square = A * square(w*t);

% Write PCM data to files
audiowrite('output/sawtooth.wav', x_sawtooth, fs);
audiowrite('output/square.wav', x_square, fs);
audiowrite('output/sine.wav', x_sine, fs);

% Plot signals
figure(1);
subplot(2,1,1), plot(t(1:floor(length(t)*t0)), ...
                        x_sawtooth(1:floor(length(t)*t0))), grid;
title('Sawtooth periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);

subplot(2,1,2), plot(t(1:floor(length(t)*t0)), ...
                        x_square(1:floor(length(t)*t0))), grid;
title('Square periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);
