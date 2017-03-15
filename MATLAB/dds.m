% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: 1.0, February 2017
% 
% Direct digital synthesis (DDS) with MATLAB
% Frequency synthesis is used for creating waveforms from a single,
% fixed-frequency reference clock.

function[] = dds()

format compact; format shortE; clear; clc;

W = 8;                  % Number of bits
M = [1 2 10 25];        % Tuning words, M = 1 = longest period
N = 2^W;                % Number of entries in the lookup table
fs = 48e3;              % Sampling frequency
t = 0:1:N;              % "Time" samples
A = 1;                  % Amplitude
plotSignal = 1;         % plot waveforms

% Generate lookup tables for the waveform
figure(1);
for i=1:length(M)
    f0 = getFrequency(M(i),N,fs);
    phase = A * sin(2*pi*f0/fs*t);
    if (plotSignal == 1)
        subplot(length(M),1,i), plot(0:length(phase)-1,phase,'r','LineWidth',2), grid minor;
        xlabel('Phase[n] (Samples) \rightarrow');
        ylabel('Amplitude');
        legend(sprintf('Entries in lookup table=%d, M=%d', N, M(i)), 'Location','NE');
        axis([0 N -1 1]);
    end
    % Write array to ASCII-delimited file
    file = strcat('output/waveform_m', num2str(M(i)), '.txt');
    dlmwrite(file, phase);
end

    function[f] = getFrequency(M,N,fs)
        f = M*fs/N;
    end

end