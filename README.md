# Compiler
这是一个BUAA的编译课设，一个没有ui界面的SysY编译器，中间代码为LLVM-IR，目标语言是MIPS，当前已经实现了一部分llvmir指令翻译为mips指令（由于时间不充裕和数据库课设的压迫，没有完全实现，但是LLVMIR是经过严格测试的）
***
## 代码结构
Compiler.java是main函数入口，分别调用了前端frontend中的词法、语法分析、语义分析，midend中的代码生成和backend的mips生成
***
SysY的文法在文件文法.pdf中
