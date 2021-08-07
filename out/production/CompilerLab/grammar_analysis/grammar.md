P -> B  
B -> {D S}  
D -> D A | ~       
A -> C W ID; | C W print ( ) B 
C -> C[num] | int | char | float | double | short  
W -> * | ~
S -> S T | ~       
T -> L = R;  
  | if(R) T  
  | if(R) T else T  
  | do T while(R);  
  | break;  
  | B  
L -> L[num] | id  
R -> E<E | E<=E | E>E | E>=E | E  
E -> E+F | E-F | F  
F -> F*G | F/G | G  
G -> (R) | L | num  | true | false | id


文法改造（消除左递归和回溯）
P ->B  
B ->{ D S }  
D ->H  
H->A D | ~       
A ->C X
X ->id; | print ( ) B
C ->int J | char J | float J | double J | short J
J ->[ num ] J |* J |~
S ->I  
I ->T I |~     
T ->L = R ;  
  | if ( R ) T else T   
  | do T while ( R ) ;  
  | break ;  
  | B  
  | print ( G ) ; 
L ->id K
K ->[ num ] K | ~
R ->E M
M ->< E | <= E | > E | >= E | ~  
E ->F N
N ->+ F | - F | ~  
F ->G Q
Q ->* G | / G | ~ 
G ->( R ) | L | num | true | false | id
















