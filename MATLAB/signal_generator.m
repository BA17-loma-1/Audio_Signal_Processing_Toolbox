% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: February 2017
% 
% Audio signal generation with MATLAB

format compact; format shortE; clear; close all; clc;

fs = 48e3;              % Sampling frequency
T = 10;                 % Signal duration
dt = 0:1/fs:T;
f0 = 500;               % Fundamental frequency
f1 = 4000;              
f2 = 1000;
t0 = 1/f0;              % Signal period
w0 = 2*pi*f0;           % Omega (angular frequency)
w1 = 2*pi*f1;
w2 = 2*pi*f2;
A = 0.5;                % Amplitude

% Generate signals
x_sine = A * sin(w0*dt);
x_cosine = A * (cos(w0*dt) + cos(w1*dt));
x_cosine = [x_cosine; x_cosine]';
x_sawtooth = A * sawtooth(w2*dt);
x_square = A * square(w2*dt);

% Write PCM data to files
audiowrite('output/sine.wav', x_sine, fs, 'BitsPerSample', 16);
audiowrite('output/cosine.wav', x_cosine, fs, 'BitsPerSample', 16);
audiowrite('output/sawtooth.wav', x_sawtooth, fs, 'BitsPerSample', 16);
audiowrite('output/square.wav', x_square, fs, 'BitsPerSample', 16);

% Plot signals
figure(2);
subplot(4,1,1), plot(dt(1:floor(length(dt)*t0)), ...
                        x_sine(1:floor(length(dt)*t0))), grid;
title('Sine periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);

subplot(4,1,2), plot(dt(1:floor(length(dt)*t0)), ...
                        x_cosine(1:floor(length(dt)*t0))), grid;
title('Cosine periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);

subplot(4,1,3), plot(dt(1:floor(length(dt)*t0)), ...
                        x_sawtooth(1:floor(length(dt)*t0))), grid;
title('Sawtooth periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);

subplot(4,1,4), plot(dt(1:floor(length(dt)*t0)), ...
                        x_square(1:floor(length(dt)*t0))), grid;
title('Square periodic signal');
xlabel('Time [s] \rightarrow');
ylabel('Amplitude');
ylim([-1.2 1.2]);
