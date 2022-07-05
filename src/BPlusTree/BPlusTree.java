package BPlusTree;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;


public class BPlusTree<K extends Comparable<K>, V> {
	// B+树的阶，固定为4
	private static int maxNodeSize;
	// B+树的非叶子节点最大拥有的节点数量,数组的关键参数
	private static int maxNum;
	// 树的大小
	private static int size;
	// 根节点
	private Node<K, V> root;
	// 叶子结点头指针
	private Node<K, V> head;

	// 构造方法
	public BPlusTree(int maxNodeSize) { //传入阶数
		// 出现5个之后会进行拆分操作
		this.maxNodeSize = maxNodeSize;
		this.maxNum = maxNodeSize + 1;
		// 指向叶子结点头指针
		LeafNode<K, V> tmpLeafNode = new LeafNode<K, V>();
		this.root = tmpLeafNode;
		this.head = tmpLeafNode;
		this.size = 0;
	}

	// 返回叶子链表总个数
	int getSize() {
		return size;
	}

	// 以数组的形式输出叶子结点
	public Object[] getLeafNodes() {
		// 新建一个和叶子结点数目一样多的数组，用来保存输出
		Object[] arr = new Object[this.size];
		// 添加一个控制变量，初始值为头节点
		LeafNode<K, V> tmp = (LeafNode<K, V>) head;
		// pos为数组的变量参数，初始为0
		int pos = 0;
		// 链表中存在数据，不是null
		while (tmp != null) {
			// 遍历链表
			for (int i = 0; i < tmp.number; i++) {
				// System.out.print(tmp.values[i] + "");
				// 把链表的数据放回给数组
				arr[pos++] = tmp.values[i];
			}
			// 去取下一个
			tmp = tmp.next;
		}
		return arr;
	}

	// 层次输出
	public void printLevelTraverse() {
		// 没有东西
		if (root == null)
			return;
		System.out.println("层次输出key:");
		// 创建数组双向链表
		Deque<Node<K, V>> que = new ArrayDeque<Node<K, V>>();
		// 设置指针变量
		Node<K, V> last = root;
		Node<K, V> nextlast = root;
		// 把根节点放入双向链表里面
		que.push(root);
		// 循环控制
		while (que.size() != 0) {
			// 创建结点，删除并返回链表数组的最后一个元素
			Node<K, V> cur = que.pop();
			// 遍历输出
			for (int i = 0; i < cur.number; i++) {
				System.out.print(cur.keys[i] + "-");
			}
			System.out.print("  ");
			// 对中间结点进行操作输出
			for (int i = 0; i <= cur.number; i++) {
				if (cur.isLeaf == false && cur.childs[i] != null) {
					// 更新指针
					nextlast = cur.childs[i];
					que.offer(nextlast);
				}
			}
			if (last == cur) {
				System.out.println();
				last = nextlast;
			}
		}
	}

	// 根节点查询，查询执行的第一步操作
	public V find(K key) {
		V t = this.root.find(key);
		// System.out.println("这儿没有问题");
		if (t == null) {
			return null;
		}
		return t;
	}

	// 删除（入口）
	public V remove(K key) {
		V result = this.root.find(key);// 首先查找key是否存在于这颗B+树
		if (result == null) {// 如果不存在，则退出
			//System.out.println(key + " 不存在！");
			return null;// 退出
		}
		// 如果key存在，则从中间节点开始，查找对应的叶子节点，调用执行删除操作
		Node<K, V> t = this.root.remove(key);
		size--;
		//System.out.print("删除 " + key + " 成功！" + ",当前节点的key为: ");
		//for (int j = 0; j < t.number; j++) {
		//	System.out.print(t.keys[j] + " ");
		//}
		return result;
	}

	// 插入操作的入口
	public void insert(K key, V value) {
		// 没用传递进来插入的参数
		if (key == null)
			return;
		// 可以插入，不是null
		// 进行调用插入操作
		Node<K, V> temp = this.root.insert(key, value);
		// 根节点的更新
		if (temp != null) {
			this.root = temp;
		}

	}

	public int ShowRootNum() {
		return this.root.number;
	}

	// 节点父类 函数出现调用现象，Node使用抽象类
	abstract static class Node<K extends Comparable<K>, V> {
		// 父节点
		protected Node<K, V> parent;
		// 孩子结点，用数组表示
		protected Node<K, V>[] childs;
		// 孩子节点数量
		protected int number;
		// 存放的Keys
		protected Object keys[];
		
		protected boolean isLeaf;
		// 构造方法
		public Node() {
			super();
			this.keys = new Object[maxNum];
			this.number = 0;
			this.parent = null;
		}
		// 查找
		abstract V find(K key);
		// 删除
		abstract Node<K, V> remove(K key);
		// 插入
		abstract Node<K, V> insert(K key, V value);
	}

	// 非叶节点类
	static class IndexNode<K extends Comparable<K>, V> extends Node<K, V> {
		// 孩子节点，使用数组操作
		public IndexNode() {
			super();
			this.isLeaf = false;
			this.childs = new Node[maxNum + 1];
		}
		// 查找
		V find(K key) {
			// 先去寻找她的位置
			int i = 0;
			while (keys[i] != null) {
				// 不可以使用<= 存在部分数据为null
				if (((Comparable<K>) key).compareTo((K) this.keys[i]) < 0)
					break;
				i++;
			}
			return this.childs[i].find(key);
		}

		// 先把值插入到叶子节点,直到到达叶子节点才会插入
		@SuppressWarnings("unchecked")
		Node<K, V> insert(K key, V value) {
			int i = 0;
			// 可以继续插入
			while (i < this.number) {
				// 在中间结点找到对应插入位置
				if (((Comparable<K>) key).compareTo((K) this.keys[i]) <= 0)
					break;
				i++;
			}
			// 在叶子结点进行插入
			return this.childs[i].insert(key, value);
		}

		// 向上更新中间节点（递归）
		// 传进节点的左右子树和分裂出去的key
		// left为第一个结点 right为第二个结点 key为传给上层的参数
		Node<K, V> insertNode(Node<K, V> left, Node<K, V> right, K key) {

			//System.out.println("进行中间结点插入操作，插入的是：" + key);
			//System.out.println("此时parent节点的个数为：" + this.number);
			// 原来没有parent结点
			if (this.number == 0) {
				// parent的第0个位置为传递进来的key
				this.keys[0] = key;
				this.childs[0] = left;// 应该指向叶子结点的第一个
				this.childs[1] = right;// 叶子结点第二个
				this.number++;
				return this;
			}
			// 已经存在parent
			int i = 0;
			// 寻找插入位置
			while (i < this.number) {
				if (key.compareTo((K) this.keys[i]) < 0)
					break;
				i++;
			}
			// System.out.println("i= "+i);
			// 创建临时数组
			// 中间结点关键字的个数永远比孩子结点关键子的个数少一个
			// 关键字数组
			Object tmpKeys[] = new Object[maxNum];
			// 孩子结点数组
			Object tmpChilds[] = new Node[maxNum + 1];

			// 插入过程
			// 左面
			// 参数设置：左面的key不变 对应指针多移动一个
			System.arraycopy(this.keys, 0, tmpKeys, 0, i);
			System.arraycopy(this.childs, 0, tmpChilds, 0, i + 1);
			// 右面
			// 参数设置
			System.arraycopy(this.keys, i, tmpKeys, i + 1, this.number - i);
			System.arraycopy(this.childs, i + 1, tmpChilds, i + 2, this.number - i);

			// 插入
			tmpKeys[i] = key;
			tmpChilds[i + 1] = right;

			this.number++;
			// 如果中间结点的插入没有超过范围，那么把临时的复制给this
			if (this.number <= maxNodeSize) {
				System.arraycopy(tmpKeys, 0, this.keys, 0, this.number);
				System.arraycopy(tmpChilds, 0, this.childs, 0, this.number + 1);
				return null;
			}
			// 如果中间结点也需要进行拆分
			// 取中间的mid，进行拆分控制
			int mid = this.number / 2;
			// 保留原来中间的key，方便拆分后传给上层作为变量
			key = (K) this.keys[mid];

			// tmpRightNode暂时存储被拆分节点的右半部分
			IndexNode<K, V> tmpRightNode = new IndexNode<K, V>();
			// 非叶节点拆分后应该将其子节点的父节点指针更新为正确的指针
			tmpRightNode.number = this.number - mid - 1;//
			tmpRightNode.parent = this.parent;
			// 如果父节点为空,则新建
			if (this.parent == null) {
				IndexNode<K, V> tmpParentIndexNode = new IndexNode<>();
				tmpRightNode.parent = tmpParentIndexNode;
				this.parent = tmpParentIndexNode;
				//System.out.println("******parent分裂******");
			}

			// 把原来的数据传递给分开后右面的数组
			System.arraycopy(tmpKeys, mid + 1, tmpRightNode.keys, 0, mid);
			System.arraycopy(tmpChilds, mid + 1, tmpRightNode.childs, 0, mid + 1);
			// 更新指针
			for (int j = 0; j <= tmpRightNode.number; j++) {
				tmpRightNode.childs[j].parent = tmpRightNode;
			}
			// 拆分前的节点作为拆分后的左半部分节点
			this.number = mid;
			this.keys = new Object[maxNum];
			this.childs = new Node[maxNum];
			// 左面复制，还是用之前的this
			System.arraycopy(tmpKeys, 0, this.keys, 0, mid);
			System.arraycopy(tmpChilds, 0, this.childs, 0, mid + 1);

			IndexNode<K, V> parentNode = (IndexNode<K, V>) this.parent;

			return parentNode.insertNode(this, tmpRightNode, key);


		}

		// 找到对应叶子结点的remove函数，调用在叶子节点中执行删除
		@SuppressWarnings("unchecked")
		Node<K,V> remove(K key){
			int i = 0;
			//找到要删除位置i
			while (i < this.number) {
				if (((Comparable<K>) key).compareTo((K) this.keys[i]) < 0)
					break;
				i++;
			}
			return this.childs[i].remove(key);//一直递归调用，直到孩子节点为叶子节点 开始调用叶子结点的remove
		}

		//删除某个key
		void removeKey(Node<K,V> node,int i) {
			for(int j = i; j < node.number-1; j++) {
				node.keys[j] = node.keys[j+1];
				
				node.childs[j+1] = node.childs[j+2];
			}
			node.keys[node.number - 1] = null;
			node.childs[node.number] = null;
			
			node.number--;
		}
		
		// 节点删除
		Node<K,V> removeNode(K key){
			K old;
			//找到key在此叶子节点中的位置
			int i = 0;
			while (i < this.number) {
				if (((Comparable<K>) key).compareTo((K) this.keys[i]) == 0)
					break;
				i++;
			}
			//没有父节点，直接删除，退出
			if(this.parent == null) {
				this.removeKey(this, i);
				return this;
			}
			//***节点合法
			if(this.number - 1 >= (maxNodeSize+1)/2 ) {
				this.removeKey(this, i);
				//判断是否会影响此节点的最小值
				return this;
			}
			return null;
		}
	}

// 叶节点Class
	static class LeafNode<K extends Comparable<K>, V> extends Node<K, V> {

		protected Object values[];
		protected LeafNode prev;
		protected LeafNode next;

		public LeafNode() {
			super();
			this.isLeaf = true;
			this.values = new Object[maxNum];
			this.prev = null;
			this.next = null;
		}

		V find(K key) {
			if (this.number == 0)
				return null;

			// System.out.println("查找关键字"+key+"对应范围的叶子节点内容:");
			// for(int i=0;i < this.number;i++) {
			// System.out.println("数组第"+i+"个"+keys[i]+" "+values[i]+" ");
			// }
			// System.out.println();

			for (int i = 0; i < this.number; i++) {
				if (key.compareTo((K) this.keys[i]) == 0)
					return (V) this.values[i];
			}
			return null;

		}

		// @SuppressWarnings("unchecked")
		Node<K, V> insert(K key, V value) {
			// 先寻找插入数据得位置
			int i = 0;
			while (i < this.number) {
				if (((Comparable<K>) key).compareTo((K) this.keys[i]) <= 0) {
					if(((Comparable<K>) key).compareTo((K) this.keys[i]) == 0) {
						this.values[i] = value;
						return null;
					}
					else
						break;
				}
				// i的数值就是对应位置
				i++;
			}
			//if(((Comparable<K>) key).compareTo((K) this.keys[i]) == 0) {
			//	this.values[i] = value;
			//	return null;
			//}
			// 找到之后，进行添加叶子结点 利用copy进行添加
			// 插入之后的，放在一个临时数组里面，注意序号，要空出来i的位置
			System.arraycopy(this.keys, i, this.keys, i + 1, this.number - i);// i=2 4-2
			System.arraycopy(this.values, i, this.values, i + 1, this.number - i);
			// 往数组里面添加插入的值
			this.keys[i] = key;
			this.values[i] = value;
			this.number++;
			size++;
			// 不需要拆分的情况
			if (this.number <= maxNodeSize) {
				// 原来的是tmp 从0号开始 到this 存放从0开始 大小 this.number
				return null;
			}
			//System.out.println("需要进行拆分");
			// 需要拆分的情况
			int mid = this.number/2 ;
			
			// 新建tmp节点暂时存储被拆分节点的右半部分
			LeafNode<K, V> tmp = new LeafNode<K, V>();

			tmp.number = this.number - mid;
			tmp.parent = this.parent;
			// 创建parent节点
			if (this.parent == null) {
				IndexNode<K, V> tmpIndexParentNode = new IndexNode<>();
				tmp.parent = tmpIndexParentNode;
				this.parent = tmpIndexParentNode;
			}
			System.arraycopy(this.keys, mid, tmp.keys, 0, tmp.number);
			System.arraycopy(this.values, mid, tmp.values, 0, tmp.number);

			// 中间节点的Key
			K midKey = (K) tmp.keys[0];
			// 让原有叶子节点作为被拆分的左半部分

			Arrays.fill(this.keys,mid,maxNum,null);
			Arrays.fill(this.values,mid,maxNum,null);
			this.number = mid;
			//System.out.println("拆分正常！");
			// 更新链表指针
			if (this.next != null)
				this.next.prev = tmp;
			tmp.next = this.next;
			this.next = tmp;
			tmp.prev = this;

			IndexNode<K, V> parentNode = (IndexNode<K, V>) this.parent;
			
			return parentNode.insertNode(this, tmp, midKey);

		}

		// 辅助函数 删除节点中的某个key
		void removeKey(LeafNode<K,V> leaf,int i) {
			for(int j = i; j < leaf.number-1; j++) {
				leaf.keys[j] = leaf.keys[j+1];
				leaf.values[j] = leaf.values[j+1];
			}
			leaf.keys[leaf.number - 1] = null;
			leaf.values[leaf.number - 1] = null;
			leaf.number--;
		}
		//辅助函数--更新父节点的值
		void updateParent(Node<K,V> leaf,K old) {
			int k = 0;
			while(k < leaf.parent.number) {
				if (((Comparable<K>) old).compareTo((K) leaf.parent.keys[k]) == 0)
					break;
				k++;
			}
			//找到在对应父节点位置k，更新这个位置的值
			leaf.parent.keys[k] = leaf.keys[0];
		}
		//辅助函数--右借
		void borrowRight(LeafNode<K,V> leaf,LeafNode<K,V> rightNode) {
			leaf.keys[leaf.number] = rightNode.keys[0];
			leaf.values[leaf.number] = rightNode.values[0];
			leaf.number++;
			//删除右边被左边借去的key
		    K old = (K) rightNode.keys[0];
			leaf.removeKey((LeafNode<K,V>)rightNode, 0);
			//接着更新右边父结点的值
			leaf.updateParent(rightNode,old);
		}
		//辅助函数--左借
		void borrowLeft(LeafNode<K,V> leaf,LeafNode<K,V> leftNode) {
			K old = (K) leaf.keys[0];
			//左借--借来放到第一个
			for(int j = leaf.number; j > 0; j--) {
				leaf.keys[j] = leaf.keys[j-1];
				leaf.values[j] = leaf.values[j-1];
			}
			//左边的拿过来放到第一个
			leaf.keys[0] = leftNode.keys[leftNode.number - 1];
			leaf.values[0] = leftNode.values[leftNode.number - 1];
			
			leaf.number ++ ;
			//先更新父节点（因为放到了第一个）
			leaf.updateParent(leaf,old);
			//删除左边被借走的值
			leftNode.removeKey(leftNode, leftNode.number - 1);
		}
		//辅助函数--合并
		void combine(LeafNode<K,V> leftNode,LeafNode<K,V> rightNode) {
			leftNode.next = rightNode.next;
			//把右边的节点的值放到此节点
			for(int j = leftNode.number,k = 0 ; j < leftNode.number+rightNode.number;j++,k++) {
				leftNode.keys[j] = rightNode.keys[k];
				leftNode.values[j] = rightNode.values[k];
			}
			//更新这个节点的值
			leftNode.number = leftNode.number + rightNode.number;
		}
		
		@SuppressWarnings("unchecked")
		Node<K,V> remove(K key){
			//每次删除前保留要删除节点的keys[0]
			K old;
			//找到key在此叶子节点中的位置
			int i = 0;
			while (i < this.number) {
				if (((Comparable<K>) key).compareTo((K) this.keys[i]) == 0)
					break;
				i++;
			}
			//如果这个节点没有父节点 直接删除,退出
			if(this.parent == null) {
				this.removeKey(this, i);
				return this;
			}
			//i为key的位置
			/***如果叶子节点合法***/
			if((this.number - 1) >= (maxNodeSize+1)/2 ) {
				old = (K) this.keys[0];
				//先调用辅助函数将这个值删除
				this.removeKey(this, i);
				//判断是否会影响此节点的最小值
				if(i == 0) {
					this.updateParent(this, old);
				}
				return this;
			}
			/****如果叶子节点不合法****/
			//先找到与当前节点相邻的同一个父节点的子节点，判断相邻节点减一之后是否合法
			int a = 0;
			//找到当前节点是parent的child位置i
			while (a < this.parent.number) {
				if (((Comparable<K>) key).compareTo((K) this.parent.keys[a]) < 0)
					break;
				a++;
			}
			//判断这个是父节点的孩子结点的第几个
			//***如果是第一个孩子节点，只能与右边兄弟节点借或与右边兄弟节点合并
			if(a == 0) {
				//看右边兄弟节点是否合法，合法，借；不合法，合并；
				LeafNode<K,V> rightNode = (LeafNode<K, V>) this.parent.childs[1];
				if((rightNode.number - 1) >= (maxNodeSize+1)/2 ) {//合法--向右边兄弟节点借
					//右借
					this.borrowRight(this, rightNode);
					old = (K)this.keys[0];
					this.removeKey(this, i);
					if(i == 0) {
						this.updateParent(this, old);
					}
					return this;
					
				}else {//不合法--和右边的兄弟节点合并
					this.combine(this, rightNode);
					//执行删除操作
					old = (K) this.keys[0];
					this.removeKey(this, i);
					if(i == 0) {
						this.updateParent(this,old);
					}
					//在parent节点中更新（删除）被合并掉的右边的节点
					IndexNode<K,V> parentNode = (IndexNode<K, V>) this.parent;
					return parentNode.removeNode((K)rightNode.keys[0]);
				}
			}//***如果是最后一个节点，只能与左边兄弟借或与左边兄弟节点合并
			else if(a == this.parent.number){
				LeafNode<K,V> leftNode = (LeafNode<K, V>) this.parent.childs[a-1];
				if((leftNode.number - 1) >= (maxNodeSize+1)/2 ) {//合法--借
					//左借
					this.borrowLeft(this, leftNode);
					old = (K) this.keys[0];
					this.removeKey(this, i+1);//借过来的放在了第一个，要删除的key值向后移了一个
					//是否需要更新父节点
					return this;
				}
				else {//*******不合法---与左边节点合并
					this.combine(leftNode, this);
					old = (K) leftNode.keys[0];
					this.removeKey(leftNode, i + leftNode.number);
					if(i==0) {
						this.updateParent(leftNode, old);
					}
					IndexNode<K,V> parentNode = (IndexNode<K, V>) this.parent;
					return parentNode.removeNode((K)this.keys[0]);
				}
				
			}else {
				LeafNode<K,V> leftNode = (LeafNode<K, V>) this.parent.childs[a-1];
				LeafNode<K,V> rightNode = (LeafNode<K, V>)this.parent.childs[a+1];
				if((rightNode.number - 1) >= (maxNodeSize+1)/2 ) {
					//右借
					this.borrowRight(this, rightNode);
					old = (K)this.keys[0];
					this.removeKey(this, i);
					if(i == 0) {
						this.updateParent(this, old);
					}
					return this;
				}
				else if((leftNode.number - 1) >= (maxNodeSize+1)/2 ) {
					//左借
					this.borrowLeft(this, leftNode);
					old = (K) this.keys[0];
					this.removeKey(this, i+1);//借过来的放在了第一个，要删除的key值向后移了一个
					//是否需要更新父节点
					return this;
				}
				else {//合并
					this.combine(this, rightNode);
					//执行删除操作
					old = (K) this.keys[0];
					this.removeKey(this, i);
					if(i == 0) {
						this.updateParent(this,old);
					}
					//在parent节点中更新（删除）被合并掉的右边的节点
					IndexNode<K,V> parentNode = (IndexNode<K, V>) this.parent;
					return parentNode.removeNode((K)rightNode.keys[0]);
				}
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("==========test2==========");
		//BPlusTree<Integer, Integer> test2 = new BPlusTree<>(4);
		BPlusTree test2 = new BPlusTree(4);
		int[] arr= {11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 24, 25, 26};
	
		for(int i = 0;i < arr.length; i++) {
			test2.insert(arr[i], arr[i]);
		}
		System.out.println("删除前：");
		System.out.println("关键字  "+ 11 +" 的查找结果是:"+test2.find(11));
		System.out.println("关键字  "+ 47 +" 的查找结果是:"+test2.find(47));
		test2.printLevelTraverse();
		System.out.println("从头结点输出链表:");
		Object[] arr2 = test2.getLeafNodes();
		for(int i=0;i<arr2.length;i++) {
			System.out.print(arr2[i]+" ");
		}
		test2.remove(47);
		System.out.println();
		System.out.println("删除后：");
		System.out.println("关键字  "+ 11 +" 的查找结果是:"+test2.find(11));
		System.out.println("关键字  "+ 47 +" 的查找结果是:"+test2.find(47));
		test2.printLevelTraverse();
		System.out.println("从头结点输出链表:");
		Object[] arr3 = test2.getLeafNodes();
		for(int i=0;i<arr3.length;i++) {
			System.out.print(arr3[i]+" ");
		}
		
	}
	
}