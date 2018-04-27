<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <!-- PLATFORM:平台, ENTERPRISE:企业 -->
      <template v-if="res['FUNCTION'].indexOf('manager-vehicle-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="showForm()">添加车辆</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="search.keyStr" placeholder="车辆编号/车辆型号/车辆品牌/车辆产地/生产商ID/生产商名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.vehicleStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.isBind" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchIsBindList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template v-if="key_user_info.userType === 'ENTERPRISE'">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-edit" type="primary" size="small" @click="returnVehicleForms(key_user_info)">批量归还车辆</el-button>
        </div>
      </template>
    </div>

    <el-table :data="list" class="vehicleHeight">
      <el-table-column prop="vehicleCode" label="编号"></el-table-column>
      <el-table-column prop="vehiclePn" label="型号"></el-table-column>
      <el-table-column prop="vehicleBrand" label="品牌"></el-table-column>
      <el-table-column prop="vehicleMadeIn" label="车辆产地"></el-table-column>
      <el-table-column prop="orgName" label="所属单位"></el-table-column>
      <el-table-column prop="mfrsName" label="生产商"></el-table-column>
      <el-table-column prop="vehicleStatusText" label="状态">
        <template slot-scope="{row}">
          <template v-if="row.vehicleStatus === 'NORMAL'"><span style="color:#17BE45">正常</span></template>
          <template v-else-if="row.vehicleStatus === 'FREEZE'"><span style="color:red">冻结/维保</span></template>
          <template v-else><span style="color:red">作废</span></template>
        </template>
      </el-table-column>
      <!-- PLATFORM:平台, ENTERPRISE:企业, INDIVIDUAL:'个人' -->
      <!-- 管理员查看的信息 -->
      <template v-if="key_user_info.userType === 'PLATFORM'">
        <!-- v-show="key_user_info.userType === 'PLATFORM' || key_user_info.userType === 'ENTERPRISE'" -->
        <el-table-column label="电池" width="150">
          <template v-if="row.vehicleStatus === 'NORMAL'" slot-scope="{row}">
            <el-button v-if="!row.batteryId" type="text" @click="showBindForm(row)">绑定</el-button>
            <el-button v-else type="text" @click="handleUnbind(row)">解绑</el-button>
            <el-button v-if="row.batteryId" icon="el-icon-search" size="mini" type="text" @click="showHoldBindBatteryForm(row)">查看电池</el-button>
          </template>
        </el-table-column>
        <el-table-column label="配件" width="200">
          <template v-if="row.vehicleStatus === 'NORMAL'" slot-scope="{row}">
            <el-button icon="el-icon-plus" size="mini" type="text" @click="showBindPartForm(row)">添加配件</el-button>
            <el-button v-if="row.partCount > 0" icon="el-icon-search" size="mini" type="text" @click="showHoldBindPartForm(row)">查看配件</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="">
          <template slot-scope="{row}">
            <el-button icon="el-icon-edit" size="mini" type="text" @click="showEditForm(row)">编辑</el-button>
          </template>
        </el-table-column>
      </template>
      <!-- 企业和个人查看的信息 -->
      <template v-if="key_user_info.userType === 'ENTERPRISE' || key_user_info.userType === 'INDIVIDUAL'">
        <el-table-column label="电池" width="150">
          <template v-if="row.vehicleStatus === 'NORMAL'" slot-scope="{row}">
            <el-button v-if="row.batteryId" icon="el-icon-search" size="mini" type="text" @click="showHoldBindBatteryForm(row)">查看电池</el-button>
          </template>
        </el-table-column>
        <el-table-column label="配件" width="200">
          <template v-if="row.vehicleStatus === 'NORMAL'" slot-scope="{row}">
            <el-button v-if="row.partCount > 0" icon="el-icon-search" size="mini" type="text" @click="showHoldBindPartForm2(row)">查看配件</el-button>
          </template>
        </el-table-column>
      </template>
    </el-table>

    <el-pagination v-if="total" style="margin-top:10px;"
      @size-change="handleSizeChange"
      @current-change="reload"
      :current-page.sync="currentPage"
      :page-sizes="pageSizes"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>

    <!-- 添加车辆 -->
    <el-dialog title="添加车辆" :visible.sync="formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form" :rules="rules1">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="vehicleCode" label="编号">
              <el-input v-model="form.vehicleCode" auto-complete="off" :disabled="vehicleIdForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehiclePn" :rules="[{required:true, message:'请填写型号'}]" label="型号">
              <el-input v-model="form.vehiclePn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleBrand" :rules="[{required:true, message:'请填写品牌'}]" label="品牌">
              <el-input v-model="form.vehicleBrand" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleMadeIn" :rules="[{required:true, message:'请填写产地'}]" label="产地">
              <el-input v-model="form.vehicleMadeIn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="mfrsId" :rules="[{required:true, message:'请选择生产商'}]" label="生产商">
              <el-select v-model="form.mfrsId" placeholder="请选择生产商" style="width:100%;">
                <el-option v-for="o in mfrsList" :key="o.id" :label="o.mfrsName" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.vehicleStatus" placeholder="请选择状态" style="width:100%;">
                <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="flag" label="电池信息">
              <el-select v-model="flag" placeholder="请选择状态" style="width:100%;" @change="onBatteryChange(flag)">
                <el-option v-for="o in isBattery" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template v-if="flag === '0'">
        <el-form class="edit-form" :model="batteryForm" ref="batteryForm">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-form-item prop="batteryCode" :rules="[{required:true, message:'请填写编号'}]" label="电池编号">
                <el-input v-model="batteryForm.batteryCode" auto-complete="off" :disabled="form.id"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryName" :rules="[{required:true, message:'请填写电池货名'}]" label="电池货名">
                <el-input v-model="batteryForm.batteryName" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryBrand" :rules="[{required:true, message:'请填写品牌'}]" label="电池品牌">
                <el-input v-model="batteryForm.batteryBrand" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryPn" :rules="[{required:true, message:'请填写型号'}]" label="电池型号">
                <el-input v-model="batteryForm.batteryPn" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryParameters" :rules="[{required:true, message:'请填写参数'}]" label="电池参数">
                <el-input v-model="batteryForm.batteryParameters" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="mfrsId" :rules="[{required:true, message:'请选择生产商'}]" label="电池生产商">
                <el-select v-model="batteryForm.mfrsId" placeholder="请选择生产商" style="width:100%;">
                  <el-option v-for="o in mfrsList" :key="o.id" :label="o.mfrsName" :value="o.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryStatus" :rules="[{required:true, message:'请选择状态'}]" label="电池状态">
                <el-select v-model="batteryForm.batteryStatus" placeholder="请选择状态" style="width:100%;">
                  <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </template>
      <template v-if="flag === '1'">
        <el-form class="edit-form" :model="bindForm" ref="bindForm">
          <el-form-item prop="id" :rules="[{required:true, message:'请选择电池'}]" label="电池">
            <el-select style="width:100%;" v-model="bindForm.id" filterable remote placeholder="请输入电池 电池编号、电池货名、电池品牌、电池型号、电池参数、生产商ID、生产商名" @focus="remoteBattery('')" :loading="bindForm_batteryLoading">
              <el-option v-for="o in bindForm_batteryList" :key="o.id" :label="`${o.batteryBrand}-${o.batteryCode}`" :value="o.id">
                <span style="float: left">{{ o.batteryBrand }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ o.batteryCode }}</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </template>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeForm">取消</el-button>
        <el-button type="primary" @click="saveForm">添加</el-button>
      </span>
    </el-dialog>
    <!-- 编辑车辆信息 -->
    <el-dialog title="编辑车辆" :visible.sync="editFormVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="editForm" ref="editForm">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="vehicleCode" :rules="[{required:true, message:'请填写编号'}]" label="编号">
              <el-input v-model="editForm.vehicleCode" auto-complete="off" :disabled="vehicleIdForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehiclePn" :rules="[{required:true, message:'请填写型号'}]" label="型号">
              <el-input v-model="editForm.vehiclePn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleBrand" :rules="[{required:true, message:'请填写品牌'}]" label="品牌">
              <el-input v-model="editForm.vehicleBrand" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleMadeIn" :rules="[{required:true, message:'请填写产地'}]" label="产地">
              <el-input v-model="editForm.vehicleMadeIn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="mfrsId" :rules="[{required:true, message:'请选择生产商'}]" label="生产商">
              <el-select v-model="editForm.mfrsId" placeholder="请选择生产商" style="width:100%;">
                <el-option v-for="o in mfrsList" :key="o.id" :label="o.mfrsName" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="editForm.vehicleStatus" placeholder="请选择状态" style="width:100%;">
                <el-option v-for="o in statusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeEditForm">取消</el-button>
        <el-button type="primary" @click="saveEditForm">保存</el-button>
      </span>
    </el-dialog>
    <el-dialog title="绑定电池" :visible.sync="bindFormVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="bindForm" ref="bindForm">
        <el-form-item prop="batteryId" :rules="[{required:true, message:'请选择电池'}]" label="电池">
          <el-select style="width:100%;" v-model="bindForm.batteryId" filterable remote placeholder="请输入电池 电池编号、电池货名、电池品牌、电池型号、电池参数、生产商ID、生产商名" @focus="remoteBattery('')" :loading="bindForm_batteryLoading">
            <el-option v-for="o in bindForm_batteryList" :key="o.id" :label="`${o.batteryBrand}-${o.batteryCode}`" :value="o.id">
              <span style="float: left">{{ o.batteryBrand }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ o.batteryCode }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeBindForm">取消</el-button>
        <el-button type="primary" @click="saveBindForm">{{form.id ? '保存' : '绑定'}}</el-button>
      </span>
    </el-dialog>
    <!-- 查看所有未绑定配件-->
    <el-dialog title="配件列表" :visible.sync="allPartsFormVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:500px;" v-model="partsSearch.keyStr" placeholder="配件编码/配件货名/配件品牌/配件型号/配件参数/生产商ID/生产商名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="partsSearch.partsStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in searchPartsStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="partsSearch.partsType" placeholder="请选择配件" style="width:100%;">
            <el-option v-for="o in searchPartsTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="partsList" style="margin-top:10px;">
        <el-table-column prop="partsCode" label="编码"></el-table-column>
        <el-table-column prop="partsName" label="配件货名"></el-table-column>
        <el-table-column prop="partsBrand" label="品牌"></el-table-column>
        <el-table-column prop="partsPn" label="型号"></el-table-column>
        <el-table-column prop="partsTypeText" label="类别"></el-table-column>
        <el-table-column prop="partsParameters" label="参数"></el-table-column>
        <el-table-column prop="mfrsName" label="生产商"></el-table-column>
        <el-table-column prop="partsStatusText" label="状态"></el-table-column>
        <template v-if="res['FUNCTION'].indexOf('manager-parts-modify') >= 0">
          <el-table-column label="操作" width="100">
            <template slot-scope="{row}">
              <el-button v-if="!row.vehicleId" type="primary" @click="bindPart(row)">绑定</el-button>
            </template>
          </el-table-column>
        </template>
      </el-table>
      <el-pagination v-if="partsTotal" style="margin-top:10px;"
        @size-change="partsHandleSizeChange"
        @current-change="partsReload"
        :current-page.sync="partsCurrentPage"
        :page-sizes="partsPageSizes"
        :page-size="partsPageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="partsTotal">
      </el-pagination>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closePartsForm">关闭</el-button>
      </span>
    </el-dialog>
    <!-- 查看当前车辆下已绑定的配件 -->
    <el-dialog title="已有配件" :visible.sync="bindPartsFormVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <el-table :data="holdPartsList" style="width: 100%;margin-top:10px;">
        <el-table-column prop="partsCode" label="编码"></el-table-column>
        <el-table-column prop="partsName" label="配件货名"></el-table-column>
        <el-table-column prop="partsBrand" label="品牌"></el-table-column>
        <el-table-column prop="partsPn" label="型号"></el-table-column>
        <el-table-column prop="partsTypeText" label="类别"></el-table-column>
        <el-table-column prop="partsParameters" label="参数"></el-table-column>
        <el-table-column prop="mfrsName" label="生产商"></el-table-column>
        <el-table-column prop="partsStatusText" label="状态"></el-table-column>
        <template v-if="res['FUNCTION'].indexOf('manager-parts-modify') >= 0">
          <el-table-column label="操作" width="100">
            <template slot-scope="{row}">
              <el-button type="info"  @click="unbindPart(row)">解绑</el-button>
            </template>
          </el-table-column>
        </template>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="holdSavePartsForm">关闭</el-button>
      </span>
    </el-dialog>
    <!-- 企业批量归还车辆 -->
    <el-dialog title="批量归还车辆" :visible.sync="returnVehicleFormVehicle" :close-on-click-modal="false">
      <el-form class="edit-form" :model="returnVehicleForm" ref="returnVehicleForm" :rules="rules2">
        <el-form-item prop="count" style="margin-top:10px;height:30px;" label="归还数量">
          <el-input-number v-model="returnVehicleForm.count" :step="1"></el-input-number>
          <template>
            <span style="margin-left:20px">(当前企业闲置 {{ vehicleNumTotal }} 辆车)</span>
          </template>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="returnVehicleFormVehicle = false">取消</el-button>
        <el-button type="primary" @click="saveReturnVehicleForm">确定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="已有电池" :visible.sync="bindBatteryFormVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <el-table :data="batteryList" style="width: 100%;margin-top:10px;">
        <el-table-column prop="batteryCode" label="编号"></el-table-column>
        <el-table-column prop="batteryName" label="电池货名"></el-table-column>
        <el-table-column prop="batteryBrand" label="品牌"></el-table-column>
        <el-table-column prop="batteryPn" label="型号"></el-table-column>
        <el-table-column prop="batteryParameters" label="参数"></el-table-column>
        <el-table-column prop="mfrsName" label="生产商"></el-table-column>
        <el-table-column prop="batteryStatusText" label="状态"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="saveBatteryForm">关闭</el-button>
      </span>
    </el-dialog>
    <el-dialog title="已有配件" :visible.sync="bindPartsFormVisible2" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <el-table :data="holdPartsList2" style="width: 100%;margin-top:10px;">
        <el-table-column prop="partsCode" label="编码"></el-table-column>
        <el-table-column prop="partsName" label="配件货名"></el-table-column>
        <el-table-column prop="partsBrand" label="品牌"></el-table-column>
        <el-table-column prop="partsPn" label="型号"></el-table-column>
        <el-table-column prop="partsTypeText" label="类别"></el-table-column>
        <el-table-column prop="partsParameters" label="参数"></el-table-column>
        <el-table-column prop="mfrsName" label="生产商"></el-table-column>
        <el-table-column prop="partsStatusText" label="状态"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="saveBatteryForm2">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import _ from 'lodash';
import {
  mapState,
} from 'vuex';

export default {
  data() {
    // 车辆表单效验
    const checkVehicleNum = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('数量不能为空'));
      }
      setTimeout(() => {
        if (!/^\d+$/.test(value)) {
          callback(new Error('请输入非负正整数'));
        } else {
          callback();
        }
      }, 500);
      return false;
    };
    const checkVehicleId = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('编号不能为空'));
      }
      setTimeout(() => {
        if (/[\u4E00-\u9FA5]/g.test(value)) {
          callback(new Error('编号不能为汉字'));
        } else {
          callback();
        }
      }, 500);
      return false;
    };
    return {
      loading: false,
      vehicleNumTotal: undefined,
      // 车辆
      returnVehicleFormVehicle: false,
      returnVehicleForm: {
        count: 1,
      },
      rules1: {
        vehicleCode: [
          { required: true, message: '请填写编码' },
          { validator: checkVehicleId, trigger: 'blur' },
        ],
      },
      rules2: {
        count: [
          { validator: checkVehicleNum, trigger: 'blur' },
        ],
      },

      // 配件
      partsSearch: {
        partsStatus: 'NORMAL',
        partsType: '',
      },
      searchPartsTypeList: [
        { id: '', name: '全部配件' },
        { id: 'SEATS', name: '车座' },
        { id: 'FRAME', name: '车架' },
        { id: 'HANDLEBAR', name: '车把' },
        { id: 'BELL', name: '车铃' },
        { id: 'TYRE', name: '轮胎' },
        { id: 'PEDAL', name: '脚蹬' },
        { id: 'DASHBOARD', name: '仪表盘' },
      ],
      partsTypeList: [
        { id: 'SEATS', name: '车座' },
        { id: 'FRAME', name: '车架' },
        { id: 'HANDLEBAR', name: '车把' },
        { id: 'BELL', name: '车铃' },
        { id: 'TYRE', name: '轮胎' },
        { id: 'PEDAL', name: '脚蹬' },
        { id: 'DASHBOARD', name: '仪表盘' },
      ],
      partsStatusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
      searchPartsStatusList: [
        { id: 'NORMAL', name: '正常' },
      ],
      partsList: [],
      holdPartsList: [],
      allPartsFormVisible: false,
      bindPartsFormVisible: false,
      bindPartsForm: {},
      unBindPartsForm: {},

      partsPageSizes: [10, 20, 50, 100],
      partsCurrentPage: 1,
      partsPageSize: 5,
      partsTotal: 0,

      // 车辆
      list: [],
      search: {
        vehicleStatus: '',
        isBind: '',
      },
      pageSizes: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      form: {},
      formVisible: false,

      vehicleIdForm: false,
      bindFormVisible: false,
      bindForm: {},
      bindForm_batteryList: [],
      bindForm_batteryLoading: false,
      // 编辑车辆
      editForm: {},
      editFormVisible: false,

      typeList: [
        { id: 'VEHICLE', name: '车辆' },
        { id: 'BATTERY', name: '电池' },
        { id: 'PARTS', name: '配件' },
      ],
      statusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
      searchStatusList: [
        { id: '', name: '全部状态' },
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
      searchIsBindList: [
        { id: '', name: '全部' },
        { id: 'UNBIND', name: '未绑定电池' },
        { id: 'BIND', name: '已绑定电池' },
      ],
      mfrsList: [],
      // 电池
      flag: '2',
      isBattery: [
        { id: '0', name: '新电池' },
        { id: '1', name: '旧电池' },
        { id: '2', name: '无电池' },
      ],
      batteryForm: {},
      bindBatteryFormVisible: false,
      bindPartsFormVisible2: false,
      batteryList: [],
      holdPartsList2: [],
      batteryStatusList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
      ],
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      key_res_info: state => state.key_res_info,
      res: state => _.mapValues(_.groupBy(state.key_res_info, 'resType'), o => _.map(o, 'resCode')),
    }),
  },
  watch: {
    search: {
      async handler() {
        await this.reload();
      },
      deep: true,
    },
    partsSearch: {
      async handler() {
        await this.partsReload();
      },
      deep: true,
    },
  },
  methods: {
    // 电池信息
    saveBatteryForm() {
      this.bindBatteryFormVisible = false;
    },
    saveBatteryForm2() {
      this.bindPartsFormVisible2 = false;
    },
    async showHoldBindBatteryForm(row) {
      this.bindBatteryFormVisible = true;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getbypk', {
          id: row.id, flag: 'true',
        })).body;
        if (code !== '200') throw new Error(message);
        this.batteryList = _.map(respData.bizBatteries, o => ({
          ...o,
          batteryStatusText: (_.find(this.statusList, { id: o.batteryStatus }) || { name: o.batteryStatus }).name,
        }));
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 车辆
    // 批量归还车辆
    async returnVehicleForms({ orgId }) {
      const $form = this.$refs.returnVehicleForm;
      if ($form) $form.resetFields();
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/orgCountVehicle', { orgId })).body;
        if (code !== '200') throw new Error(message);
        if (code === '200') {
          this.returnVehicleForm = { orgId: orgId, count: respData };
          this.vehicleNumTotal = respData;
          this.returnVehicleFormVehicle = true;
        } else {
          this.returnVehicleFormVehicle = false;
        }
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 确定归还车辆
    async saveReturnVehicleForm() {
      const $form = this.$refs.returnVehicleForm;
      await $form.validate();
      try {
        const { ...form } = this.returnVehicleForm;
        form.count = String(this.returnVehicleForm.count);
        const { code, message } = (await this.$http.post('/api/manager/user/batchvehicleunbind', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('归还车辆成功');
        await this.reload();
        this.returnVehicleFormVehicle = false;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },

    // 配件———————————————————————————————————————————————————————————————————————————————————————————————————————
    // 关闭配件列表
    async holdClosePartsForm() {
      // await this.partsReload();
      this.bindPartsFormVisible = false;
    },
    // 保存配件列表
    async holdSavePartsForm() {
      this.bindPartsFormVisible = false;
    },
    // 分页下拉
    async partsHandleSizeChange(partsPageSize) {
      this.partsPageSize = partsPageSize;
      await this.partsReload();
    },
    // 弹出配件列表页面
    async showBindPartForm(row) {
      this.allPartsFormVisible = true;
      this.bindPartsForm = { vehicleId: row.id };
      await this.partsReload();
    },
    // 绑定配件
    async bindPart(row) {
      const { ...form } = this.bindPartsForm;
      form.partsId = row.id;
      try {
        const { code, message } = (await this.$http.post('/api/manager/parts/partBind', form)).body;
        if (code !== '200') throw new Error(message);
        await this.partsReload();
        await this.reload();
        this.$message.success('绑定成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 查看当前车辆配件
    async showHoldBindPartForm(row) {
      this.unBindPartsForm = { vehicleId: row.id };
      this.bindPartsFormVisible = true;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getbypr', [row.id])).body;
        if (code !== '200') throw new Error(message);
        this.holdPartsList = _.map(respData, o => ({
          ...o,
          partsTypeText: (_.find(this.partsTypeList, { id: o.partsType }) || { name: o.partsType }).name,
          partsStatusText: (_.find(this.partsStatusList, { id: o.partsStatus }) || { name: o.partsStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async showHoldBindPartForm2(row) {
      this.bindPartsFormVisible2 = true;
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getbypr', [row.id])).body;
        if (code !== '200') throw new Error(message);
        this.holdPartsList2 = _.map(respData, o => ({
          ...o,
          partsTypeText: (_.find(this.partsTypeList, { id: o.partsType }) || { name: o.partsType }).name,
          partsStatusText: (_.find(this.partsStatusList, { id: o.partsStatus }) || { name: o.partsStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 解绑配件
    async unbindPart(row) {
      const { ...form } = this.unBindPartsForm;
      form.partsId = row.id;
      try {
        const { code, message } = (await this.$http.post('/api/manager/parts/partsUnBind', form)).body;
        if (code !== '200') throw new Error(message);
        // row.id = form.vehicleId;
        await this.showHoldBindPartForm({ ...row, id: form.vehicleId });
        await this.reload();
        this.$message.success('解绑成功');
        // await this.partsReload();
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 关闭配件页面
    closePartsForm() {
      this.allPartsFormVisible = false;
    },
    // 保存配件页面
    async savePartsForm() {
      this.$message.success('保存成功');
      this.allPartsFormVisible = false;
    },
    async handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      await this.reload();
    },
    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/list', {
          currPage: this.currentPage, pageSize: this.pageSize, ...this.search,
        })).body;
        if (code === '40106') {
          this.$store.commit('relogin');
          throw new Error('认证超时，请重新登录');
        }
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.total = total;
        this.list = _.map(rows, o => ({
          ...o,
          vehicleStatusText: (_.find(this.statusList, { id: o.vehicleStatus }) || { name: o.vehicleStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 显示配件信息
    async partsReload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/parts/list', {
          currPage: this.partsCurrentPage, pageSize: this.partsPageSize, ...this.partsSearch, isBind: 'UNBIND',
        })).body;
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.partsTotal = total;
        this.partsList = _.map(rows, o => ({
          ...o,
          partsTypeText: (_.find(this.partsTypeList, { id: o.partsType }) || { name: o.partsType }).name,
          partsStatusText: (_.find(this.partsStatusList, { id: o.partsStatus }) || { name: o.partsStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleDelete({ id, resName }) {
      try {
        await this.$confirm(`确认删除${resName}, 是否继续?`, '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/vehicle/delete', [id])).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('删除成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    showForm(form = { }) {
      const $form = this.$refs.form;
      if ($form) $form.resetFields();
      this.form = _.pick(form, [
        'id',
        'vehicleCode',
        'vehiclePn',
        'vehicleBrand',
        'vehicleMadeIn',
        'mfrsId',
        'vehicleStatus',
      ]);
      this.flag = '2';
      if (form.id) {
        this.vehicleIdForm = true;
      } else {
        this.vehicleIdForm = false;
      }
      this.formVisible = true;
    },
    closeForm() {
      this.formVisible = false;
    },
    async saveForm() {
      const { loginName } = this.key_user_info;
      try {
        if (this.flag === '0') {
          const $form = this.$refs.form;
          await $form.validate();

          const $batteryForm = this.$refs.batteryForm;
          await $batteryForm.validate();
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { ...batteryForm } = this.batteryForm;
          batteryForm.create_user = loginName;
          batteryForm.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/vehicle/addone', {
            bizVehicleInfo: form, flag: this.flag, batteryInfo: batteryForm,
          })).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('添加成功');
        } else if (this.flag === '1') {
          const $form = this.$refs.form;
          await $form.validate();

          const $bindForm = this.$refs.bindForm;
          await $bindForm.validate();
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { ...bindForm } = this.bindForm;
          const { code, message } = (await this.$http.post('/api/manager/vehicle/addone', {
            bizVehicleInfo: form, flag: this.flag, batteryInfo: bindForm,
          })).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('添加成功');
        } else if (this.flag === '2') {
          const $form = this.$refs.form;
          await $form.validate();
          const { ...form } = this.form;
          if (form.parent === '') form.parent = null;
          form.create_user = loginName;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/vehicle/addone', {
            bizVehicleInfo: form, flag: this.flag,
          })).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('添加成功');
        }
        await this.reload();
        this.closeForm();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 编辑车辆Form
    showEditForm(form = { }) {
      const $form = this.$refs.editForm;
      if ($form) $form.resetFields();
      this.editForm = _.pick(form, [
        'id',
        'vehicleCode',
        'vehiclePn',
        'vehicleBrand',
        'vehicleMadeIn',
        'mfrsId',
        'vehicleStatus',
      ]);
      this.flag = '2';
      if (form.id) {
        this.vehicleIdForm = true;
      } else {
        // const $form = this.$refs.form;
        // if ($form) $form.resetFields();
        this.vehicleIdForm = false;
      }
      this.editFormVisible = true;
    },
    closeEditForm() {
      this.editFormVisible = false;
    },
    async saveEditForm() {
      const { loginName } = this.key_user_info;
      try {
        const $form = this.$refs.editForm;
        await $form.validate();

        if (this.editForm.id) {
          const { ...form } = this.editForm;
          if (form.parent === '') form.parent = null;
          form.update_user = loginName;
          const { code, message } = (await this.$http.post('/api/manager/vehicle/modify', form)).body;
          if (code !== '200') throw new Error(message);
          this.$message.success('编辑成功');
        }
        await this.reload();
        this.closeEditForm();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    showBindForm({ id }) {
      const $form = this.$refs.bindForm;
      if ($form) $form.resetFields();
      this.bindForm = { vehicleId: id };
      this.bindFormVisible = true;
    },
    closeBindForm() {
      this.bindFormVisible = false;
    },
    async saveBindForm() {
      try {
        const $form = this.$refs.bindForm;
        await $form.validate();
        const { ...form } = this.bindForm;
        const { code, message } = (await this.$http.post('/api/manager/vehicle/batterybind', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('绑定成功');
        await this.reload();
        this.closeBindForm();
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 显示配件窗口
    closeBindPartsForm() {
      this.allPartsFormVisible = false;
    },
    async handleUnbind({ id, batteryId }) {
      try {
        await this.$confirm('确认解绑该车电池, 是否继续?', '提示', { type: 'warning' });
        const { code, message } = (await this.$http.post('/api/manager/vehicle/batteryunbind', { vehicleId: id, batteryId })).body;
        if (code !== '200') throw new Error(message);
        await this.reload();
        this.$message.success('解绑成功');
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async remoteBattery(keyStr) {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/battery/list', {
          keyStr, needPaging: false, batteryStatus: 'NORMAL', isBind: 'UNBIND',
        })).body;
        if (code !== '200') throw new Error(message);
        const { rows } = respData;
        this.bindForm_batteryList = _.map(rows, o => ({
          ...o,
          batteryStatusText: (_.find(this.statusList, { id: o.batteryStatus }) || { name: o.batteryStatus }).name,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
  },
  async mounted() {
    if (this.key_user_info.userType === 'PLATFORM') {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/mfrs/list', {
          currPage: 1, pageSize: 999,
        })).body;
        if (code !== '200') throw new Error(message);
        this.mfrsList = respData.rows;
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    }
    this.loading = true;
    await this.reload();
    await this.partsReload();
    this.loading = false;
  },
};
</script>

<style scoped>
.edit-form >>> .el-form-item {
  height: 55px;
}
>>> .el-table {
  position: relative;
  overflow-x: hidden;
  overflow-y: scroll;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  width: 100%;
  max-width: 100%;
  color: #606266;
  height: 85%;
  max-height: 85%;
}
</style>
