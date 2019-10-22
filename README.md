# LibRoad
Android third-party library Detection
Usage:
LibRoad accepts two inputs: an Android app (.apk file) and a list of libraries (.dex file). 
LibRoad returns a list of ARP---library pairs as results.It is possible for each ARP to have more than one lib.Since our approach applies a global searching policy to find a TPL with the highest similarity score as the perfectly matched one instead of relying on a threshold-based policy, which is helpful to minimize the possibility of false positives.
TPL Discovery from the Local Repository requires a large Local database, and LibRoad alone does not work.
About Groundtruth:
We construct a ground truth base of 2800 application-library pairs, covering 300 applications and more than 6000 TPLs, which can be used to evaluate the of the TPL detection approaches.
300 apps have been included in 300.zip and groundtruth list is in 2800groundtruth.txt
