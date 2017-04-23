% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: 1.0, February 2017
% 
% Direct digital synthesis (DDS)
% Frequency synthesis is used for creating waveforms from a single,
% fixed-frequency reference clock.

function[] = dds()

format compact; format shortE; clear; close all; clc;

W = 8;                  % Number of bits
M = [1 2 10 25];        % Tuning words, M = 1 = longest period
N = 2^W;                % Number of entries in the lookup table
fs = 48e3;              % Sampling frequency
t = 0:1:N;              % "Time" samples
A = 1;                  % Amplitude
plotSignal = 1;         % plot waveforms

% Generate lookup tables for the waveforms
for i=1:length(M)
    f0 = getFrequency(M(i),N,fs);
    
    phaseSine = A * sin(2*pi*f0/fs*t);
    phaseSawtooth = A * sawtooth(2*pi*f0/fs*t);
    phaseSquare = A * square(2*pi*f0/fs*t);
    
    % Write arrays to ASCII-delimited file
    file = strcat('output/sine_m', num2str(M(i)), '.txt');
    dlmwrite(file, phaseSine, 'delimiter', ',', 'precision', '%1.12f');
    file = strcat('output/sawtooth_m', num2str(M(i)), '.txt');
    dlmwrite(file, phaseSawtooth, 'delimiter', ',', 'precision', '%1.12f');
    file = strcat('output/square_m', num2str(M(i)), '.txt');
    dlmwrite(file, phaseSquare, 'delimiter', ',', 'precision', '%1.12f');
end

if (plotSignal == 1)
    figure(1);
    subplot(3,1,1), plot(0:length(phaseSine)-1,phaseSine,'r','LineWidth',2), grid minor;
    xlabel('Phase[n] (Samples) \rightarrow');
    ylabel('Amplitude');
    legend(sprintf('Entries in lookup table=%d, M=%d', N, M(i)), 'Location','NE');
    axis([0 N -1 1]);

    subplot(3,1,2), plot(0:length(phaseSawtooth)-1,phaseSawtooth,'r','LineWidth',2), grid minor;
    xlabel('Phase[n] (Samples) \rightarrow');
    ylabel('Amplitude');
    legend(sprintf('Entries in lookup table=%d, M=%d', N, M(i)), 'Location','NE');
    axis([0 N -1 1]);
    
    subplot(3,1,3), plot(0:length(phaseSquare)-1,phaseSquare,'r','LineWidth',2), grid minor;
    xlabel('Phase[n] (Samples) \rightarrow');
    ylabel('Amplitude');
    legend(sprintf('Entries in lookup table=%d, M=%d', N, M(i)), 'Location','NE');
    axis([0 N -1 1]);
end

    function[f] = getFrequency(M,N,fs)
        f = M*fs/N;
    end

end