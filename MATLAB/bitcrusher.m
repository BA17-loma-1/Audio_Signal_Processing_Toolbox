% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: May 2017
% 
% Audio effect: Bitcrusher

function [y] = bitcrusher(x, bits, normFreq)

y = zeros(1, length(x));
step = 0.5^bits;
phasor = 0;
last = 0;
for i=1:length(x)
    phasor = phasor + normFreq;
    if (phasor >= 1)
        phasor = phasor - 1;
        % Quantize
        last = step * floor((x(i) / step) + 0.5);
    end
    % Sample and hold
    y(i) = last;
end

end