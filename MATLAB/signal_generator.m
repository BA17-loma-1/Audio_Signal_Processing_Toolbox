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
f1 = 4000;              
t0 = 1/f0;              % Signal period
w0 = 2*pi*f0;           % Omega (angular frequency)
w1 = 2*pi*f1;
A = 0.5;                % Amplitude

% Generate signals
x_sine = A * sin(w0*t) + A * sin(w1*t);
x_sawtooth = A * sawtooth(w0*t);
x_square = A * square(w0*t);

% Write PCM data to files
audiowrite('output/sine.wav', x_sine, fs);
audiowrite('output/sawtooth.wav', x_sawtooth, fs);
audiowrite('output/square.wav', x_square, fs);

% Plot signals
figure(1);
subplot(3,1,1), plot(t(1:floor(length(t)*t0)), ...
                        x_sine(1:floor(length(t)*t0))), grid;
title('Sine periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);

subplot(3,1,2), plot(t(1:floor(length(t)*t0)), ...
                        x_sawtooth(1:floor(length(t)*t0))), grid;
title('Sawtooth periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);

subplot(3,1,3), plot(t(1:floor(length(t)*t0)), ...
                        x_square(1:floor(length(t)*t0))), grid;
title('Square periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);
