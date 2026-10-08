import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:video_player/video_player.dart';

const ink=Color(0xFF0B0F1A), panel=Color(0xFF192032), gold=Color(0xFFF0CA89);
void main()=>runApp(const Nubra());
class Post {
  Post(this.title,this.author,this.asset,{this.file});
  final String title,author,asset;
  final File? file;
  bool liked=false,saved=false,following=false;
  int likes=128;
  final List<String> comments=[];
}
class Nubra extends StatefulWidget {const Nubra({super.key}); @override State<Nubra> createState()=>_NubraState();}
class _NubraState extends State<Nubra> {
  int tab=0;
  final posts=<Post>[
    Post('حكايات الخليج تبدأ من هنا','@nubra','assets/demo1.mp4'),
    Post('لحظة مختلفة من قلب الرياض','@riyadh','assets/demo2.mp4'),
    Post('صُناع الإبداع، أهلًا بيكم','@creators','assets/demo3.mp4'),
  ];
  void alert(String s)=>ScaffoldMessenger.of(context).showSnackBar(SnackBar(content:Text(s)));
  Future<void> add() async {
    try {
      final pick=await FilePicker.platform.pickFiles(type:FileType.video);
      if(pick==null||pick.files.single.path==null)return;
      if(!mounted)return;
      final name=TextEditingController(text:'فيديو من موبايلي');
      final ok=await showDialog<bool>(context:context,builder:(ctx)=>Directionality(textDirection:TextDirection.rtl,child:AlertDialog(
        title:const Text('إضافة فيديو محلي'),content:Column(mainAxisSize:MainAxisSize.min,children:[
          TextField(controller:name,maxLength:90,decoration:const InputDecoration(labelText:'عنوان الفيديو')),
          const Text('التجربة محلية؛ الفيديو مش هيتنشر على الإنترنت.',style:TextStyle(fontSize:12))
        ]),actions:[TextButton(onPressed:()=>Navigator.pop(ctx,false),child:const Text('إلغاء')),
          FilledButton(onPressed:()=>Navigator.pop(ctx,true),child:const Text('إضافة'))])));
      if(ok==true&&mounted){setState((){posts.insert(0,Post(name.text,'@me','',file:File(pick.files.single.path!)));tab=0;});alert('اتضاف الفيديو للخلاصة المحلية');}
      name.dispose();
    }catch(e){if(mounted)alert('تعذّر اختيار الفيديو: $e');}
  }
  @override Widget build(BuildContext c)=>MaterialApp(
    debugShowCheckedModeBanner:false,title:'نُبْرة',
    theme:ThemeData(useMaterial3:true,brightness:Brightness.dark,scaffoldBackgroundColor:ink,
      colorScheme:ColorScheme.fromSeed(seedColor:gold,brightness:Brightness.dark)),
    home:Directionality(textDirection:TextDirection.rtl,child:Scaffold(
      appBar:AppBar(backgroundColor:ink,title:const Row(mainAxisSize:MainAxisSize.min,children:[
        Text('نُبْرة',style:TextStyle(color:gold,fontSize:28,fontWeight:FontWeight.w900)),
        SizedBox(width:9),Icon(Icons.auto_awesome,color:gold,size:17)]),
        actions:[IconButton(icon:const Icon(Icons.search),onPressed:()=>setState(()=>tab=1))]),
      body:IndexedStack(index:tab,children:[
        Feed(posts:posts,changed:()=>setState((){}),alert:alert),
        Discover(posts:posts,open:(p)=>setState((){posts.remove(p);posts.insert(0,p);tab=0;})),
        Builder(builder:(ctx)=>ListView(padding:const EdgeInsets.all(25),children:[
          const SizedBox(height:60),const Icon(Icons.video_call_outlined,size:94,color:gold),
          const SizedBox(height:24),const Text('خلّي حكايتك توصل',style:TextStyle(fontSize:30,fontWeight:FontWeight.w900),textAlign:TextAlign.center),
          const SizedBox(height:15),const Text('اختار فيديو من موبايلك، وشاهده داخل التطبيق. رفع الفيديو للخادم مش مفعل في معاينة APK دي.',textAlign:TextAlign.center,style:TextStyle(height:1.7)),
          const SizedBox(height:35),FilledButton.icon(onPressed:add,icon:const Icon(Icons.add),label:const Text('اختيار فيديو من الهاتف')),
        ])),
        const FeaturePage(),
        Profile(posts:posts,add:add),
      ]),
      bottomNavigationBar:NavigationBar(backgroundColor:panel,selectedIndex:tab,indicatorColor:gold.withValues(alpha:.20),
        onDestinationSelected:(i)=>setState(()=>tab=i),destinations:const [
          NavigationDestination(icon:Icon(Icons.home_outlined),label:'الرئيسية'),
          NavigationDestination(icon:Icon(Icons.explore_outlined),label:'اكتشف'),
          NavigationDestination(icon:Icon(Icons.add_circle_outline,color:gold),label:'إنشاء'),
          NavigationDestination(icon:Icon(Icons.live_tv_outlined),label:'مباشر وتلفزيون'),
          NavigationDestination(icon:Icon(Icons.person_outline),label:'حسابي'),
        ]),
    )),
  );
}
class Feed extends StatelessWidget {
  const Feed({super.key,required this.posts,required this.changed,required this.alert});
  final List<Post> posts; final VoidCallback changed;final void Function(String) alert;
  @override Widget build(BuildContext c)=>Stack(children:[
    PageView.builder(scrollDirection:Axis.vertical,itemCount:posts.length,itemBuilder:(ctx,i)=>
      Clip(key:ValueKey(posts[i]),post:posts[i],changed:changed,alert:alert)),
    Positioned(top:9,left:0,right:0,child:Center(child:Container(
      padding:const EdgeInsets.symmetric(horizontal:16,vertical:8),
      decoration:BoxDecoration(color:Colors.black54,borderRadius:BorderRadius.circular(35)),
      child:const Text('لك  •  اكتشف عالمك',style:TextStyle(fontWeight:FontWeight.w800))))),
    const Positioned(bottom:5,left:0,right:0,child:Text('معاينة محلية • المحتوى والتفاعلات تجريبية',style:TextStyle(fontSize:10),textAlign:TextAlign.center))
  ]);
}
class Clip extends StatefulWidget {
  const Clip({super.key,required this.post,required this.changed,required this.alert});
  final Post post;final VoidCallback changed;final void Function(String) alert;
  @override State<Clip> createState()=>_ClipState();
}
class _ClipState extends State<Clip> {
  VideoPlayerController? player;
  bool playing=true;
  @override void initState(){super.initState();start();}
  Future<void> start() async {
    final x=widget.post.file==null?VideoPlayerController.asset(widget.post.asset):VideoPlayerController.file(widget.post.file!);
    player=x;
    try{await x.initialize();await x.setLooping(true);await x.play();if(mounted)setState((){});}catch(_){if(mounted)setState(()=>playing=false);}
  }
  @override void dispose(){player?.dispose();super.dispose();}
  Future<void> comment() async {
    final input=TextEditingController();
    await showModalBottomSheet<void>(context:context,isScrollControlled:true,showDragHandle:true,
      builder:(ctx)=>Directionality(textDirection:TextDirection.rtl,child:Padding(
        padding:EdgeInsets.fromLTRB(20,12,20,MediaQuery.of(ctx).viewInsets.bottom+20),
        child:Column(mainAxisSize:MainAxisSize.min,children:[
          const Text('التعليقات',style:TextStyle(fontSize:22,fontWeight:FontWeight.bold)),
          const SizedBox(height:15),
          for(final s in widget.post.comments)ListTile(leading:const Icon(Icons.person,color:gold),title:const Text('أنت'),subtitle:Text(s)),
          if(widget.post.comments.isEmpty)const Padding(padding:EdgeInsets.all(20),child:Text('أول تعليق ممكن يكون منك ✨')),
          TextField(controller:input,maxLength:220,decoration:const InputDecoration(hintText:'قول رأيك...')),
          FilledButton(onPressed:(){if(input.text.trim().isEmpty)return;setState(()=>widget.post.comments.add(input.text.trim()));widget.changed();Navigator.pop(ctx);},
            child:const Text('إرسال التعليق')),
        ]))));
    input.dispose();
  }
  Widget action(IconData icon,String text,VoidCallback tap,{Color color=Colors.white})=>
    Padding(padding:const EdgeInsets.only(bottom:20),child:InkWell(onTap:tap,
      child:Column(children:[Icon(icon,size:30,color:color),const SizedBox(height:5),Text(text,style:const TextStyle(fontSize:12,fontWeight:FontWeight.w700))])));
  @override Widget build(BuildContext c)=>Stack(fit:StackFit.expand,children:[
    GestureDetector(onTap:(){if(player?.value.isInitialized!=true)return;
      setState((){if(player!.value.isPlaying){player!.pause();playing=false;}else{player!.play();playing=true;}});},
      child:Container(color:const Color(0xFF29374A),
        child:player?.value.isInitialized==true?FittedBox(fit:BoxFit.cover,
          child:SizedBox(width:player!.value.size.width,height:player!.value.size.height,child:VideoPlayer(player!))):
          const Center(child:CircularProgressIndicator(color:gold)))),
    IgnorePointer(child:Container(decoration:const BoxDecoration(gradient:LinearGradient(
      begin:Alignment.topCenter,end:Alignment.bottomCenter,colors:[Colors.black38,Colors.transparent,Colors.black87])))),
    if(!playing)const IgnorePointer(child:Center(child:Icon(Icons.play_circle_fill,size:80,color:Colors.white60))),
    Positioned(right:16,left:85,bottom:46,child:Column(crossAxisAlignment:CrossAxisAlignment.start,children:[
      Row(children:[const CircleAvatar(backgroundColor:gold,child:Icon(Icons.person,color:ink)),
        const SizedBox(width:10),Text(widget.post.author,style:const TextStyle(fontWeight:FontWeight.w800,fontSize:18)),
        const SizedBox(width:12),
        InkWell(onTap:(){setState(()=>widget.post.following=!widget.post.following);widget.changed();},
          child:Container(padding:const EdgeInsets.symmetric(horizontal:12,vertical:6),
            decoration:BoxDecoration(border:Border.all(color:gold),borderRadius:BorderRadius.circular(30)),
            child:Text(widget.post.following?'تتابع':'متابعة',style:const TextStyle(color:gold)))),
      ]),const SizedBox(height:12),Text(widget.post.title,style:const TextStyle(fontSize:21,fontWeight:FontWeight.w800)),
      const SizedBox(height:9),const Text('#نبرة   #الخليج   #اكتشف',style:TextStyle(color:gold)),
      const SizedBox(height:8),const Text('فيديو تجريبي • بدون اتصال بالخادم',style:TextStyle(fontSize:11))
    ])),
    Positioned(left:13,bottom:75,child:Column(children:[
      action(widget.post.liked?Icons.favorite:Icons.favorite_border,'${widget.post.likes}',(){
        setState((){widget.post.liked=!widget.post.liked;widget.post.likes+=widget.post.liked?1:-1;});widget.changed();},
        color:widget.post.liked?Colors.pinkAccent:Colors.white),
      action(Icons.comment_outlined,'${widget.post.comments.length}',comment),
      action(widget.post.saved?Icons.bookmark:Icons.bookmark_border,'حفظ',(){
        setState(()=>widget.post.saved=!widget.post.saved);widget.changed();},
        color:widget.post.saved?gold:Colors.white),
      action(Icons.share_outlined,'مشاركة',()=>widget.alert('مشاركة الفيديوهات تحتاج نشر الخدمة عبر الإنترنت')),
    ])),
  ]);
}
class Discover extends StatefulWidget{
  const Discover({super.key,required this.posts,required this.open});
  final List<Post> posts; final void Function(Post) open;
  @override State<Discover> createState()=>_DiscoverState();
}
class _DiscoverState extends State<Discover>{
  String q='';
  @override Widget build(BuildContext c){
    final items=widget.posts.where((p)=>('${p.title} ${p.author}').contains(q)).toList();
    return ListView(padding:const EdgeInsets.all(20),children:[
      const SizedBox(height:15),const Text('اكتشف نُبرتك',style:TextStyle(fontSize:29,fontWeight:FontWeight.w900)),
      const SizedBox(height:8),const Text('مقاطع من الخليج وصنّاع محتوى جدد',style:TextStyle(color:gold)),
      const SizedBox(height:24),TextField(onChanged:(v)=>setState(()=>q=v),decoration:InputDecoration(
        prefixIcon:const Icon(Icons.search),hintText:'ابحث عن فيديو أو مبدع',filled:true,fillColor:panel,
        border:OutlineInputBorder(borderRadius:BorderRadius.circular(18),borderSide:BorderSide.none))),
      const SizedBox(height:25),const Text('ترند نُبرة',style:TextStyle(fontSize:20,fontWeight:FontWeight.w800)),
      const SizedBox(height:14),Wrap(spacing:8,children:['الرياض','الخليج','مبدعين','فيديوهات'].map((s)=>Chip(label:Text(s),backgroundColor:panel)).toList()),
      const SizedBox(height:18),...items.map((p)=>Card(color:panel,child:ListTile(
        leading:const Icon(Icons.play_circle_outline,color:gold,size:42),title:Text(p.title),subtitle:Text(p.author),
        trailing:const Icon(Icons.chevron_left),onTap:()=>widget.open(p)))),
      if(items.isEmpty)const Center(child:Text('مفيش نتائج مطابقة')),
    ]);
  }
}
class FeaturePage extends StatelessWidget{
  const FeaturePage({super.key});
  @override Widget build(BuildContext c)=>ListView(padding:const EdgeInsets.all(22),children:[
    const SizedBox(height:30),const Icon(Icons.live_tv_outlined,size:82,color:gold),
    const SizedBox(height:20),const Text('مباشر وتلفزيون',style:TextStyle(fontSize:28,fontWeight:FontWeight.w900),textAlign:TextAlign.center),
    const SizedBox(height:16),const Text('هنضيف البث المباشر الحقيقي والقنوات المرخصة وجدول البرامج في نسخة قادمة. الواجهة دي مش بث حقيقي.',
      style:TextStyle(height:1.65),textAlign:TextAlign.center),
    const SizedBox(height:25),
    for(final s in ['بث مباشر تفاعلي','قنوات تلفزيونية رقمية','فعاليات ومنافسات'])Card(color:panel,child:ListTile(
      leading:const Icon(Icons.lock_clock,color:gold),title:Text(s),subtitle:const Text('قريبًا بعد تكامل خدمات البث'))),
  ]);
}
class Profile extends StatelessWidget{
  const Profile({super.key,required this.posts,required this.add});
  final List<Post> posts;final VoidCallback add;
  @override Widget build(BuildContext c)=>ListView(padding:const EdgeInsets.all(22),children:[
    const SizedBox(height:30),const CircleAvatar(radius:42,backgroundColor:panel,child:Icon(Icons.person,size:52,color:gold)),
    const SizedBox(height:14),const Text('مستكشف نُبرة',textAlign:TextAlign.center,style:TextStyle(fontSize:24,fontWeight:FontWeight.w900)),
    const Text('@nubra_preview',textAlign:TextAlign.center,style:TextStyle(color:gold)),
    const SizedBox(height:14),const Text('حساب معاينة محلي من غير تسجيل أو بيانات سحابية',textAlign:TextAlign.center),
    const SizedBox(height:28),Row(mainAxisAlignment:MainAxisAlignment.spaceEvenly,children:[
      Column(children:[Text('${posts.length-3}',style:const TextStyle(fontSize:26,color:gold)),const Text('مقاطع مضافة')]),
      Column(children:[Text('${posts.where((x)=>x.liked).length}',style:const TextStyle(fontSize:26,color:gold)),const Text('إعجابات')]),
      Column(children:[Text('${posts.where((x)=>x.saved).length}',style:const TextStyle(fontSize:26,color:gold)),const Text('محفوظات')])
    ]),
    const SizedBox(height:30),Card(color:panel,child:const Column(children:[
      ListTile(leading:Icon(Icons.shield_outlined,color:gold),title:Text('الخصوصية'),subtitle:Text('المحتوى المحلي لا يُرفع لخادم')),
      Divider(height:1),ListTile(leading:Icon(Icons.info_outline,color:gold),title:Text('الإصدار'),subtitle:Text('NUBRA preview v0.1')),
    ])),
    const SizedBox(height:25),FilledButton.icon(onPressed:add,icon:const Icon(Icons.add),label:const Text('أضف مقطعًا للتجربة')),
  ]);
}
