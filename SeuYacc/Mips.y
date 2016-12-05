%%

%token IDENT HEXNUM DECNUM REGISTER LOAD STORE ROP_3 ROP_2 ROP_1 ROP_0 IOP_3 IOP_2 JOP_2 JOP_1 HOP_1 .CODE .DATA SEG .BYTE .WORD .HALF .SPACE

%%

program        : decl_list                                              {}

decl_list      : decl_list decl                                         {}
               | decl                                                   {}

decl           : code_seg                                               {}
               | data_seg                                               {}

code_seg       : .CODE [ IDENT ] codes                                  {}
               | .CODE codes                                            {}

codes          : codes code                                             {}
               | code                                                   {}

code           : IDENT : code_body                                      {}
               | code_body                                              {}

code_body      : ROP_3 REGISTER , REGISTER , REGISTER                   {}
               | ROP_2 REGISTER , REGISTER                              {}
               | ROP_1 REGISTER                                         {}
               | ROP_0                                                  {}
               | IOP_3 REGISTER , REGISTER , int_literal                {}
               | IOP_3 REGISTER , REGISTER , IDENT                      {}
               | IOP_2 REGISTER , int_literal                           {}
               | JOP_2 REGISTER , REGISTER                              {}
               | JOP_1 REGISTER                                         {}
               | JOP_1 int_literal                                      {}
               | JOP_1 IDENT                                            {}
               | LOAD REGISTER , IDENT                                  {}
               | LOAD REGISTER , int_literal ( REGISTER )               {}
               | STORE REGISTER , IDENT                                 {}
               | STORE REGISTER , int_literal ( REGISTER )              {}
               | HOP_1 REGISTER                                         {}

data_seg       : .DATA [ IDENT ] datas                                  {}
               | .DATA datas                                            {}

datas          : datas data                                             {}
               | data                                                   {}

data           : IDENT : type_spec                                      {}

type_spec      : .BYTE values                                           {}
               | .WORD values                                           {}
               | .HALF values                                           {}
               | .SPACE values                                          {}

values         : values value                                           {}
               | value                                                  {}

value          : int_literal                                            {}
               | ?                                                      {}

int_literal    : DECNUM                                                 {}
               | HEXNUM                                                 {}

%%