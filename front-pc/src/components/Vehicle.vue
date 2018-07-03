<template>
  <div v-loading="loading" style="padding:10px;">
    <div style="display:flex;">
      <!-- PLATFORM:平台, ENTERPRISE:企业 -->
      <template v-if="res['FUNCTION'].indexOf('manager-vehicle-addone') >= 0">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-plus" type="primary" size="small" @click="addVehicleForm()">添加车辆</el-button>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:400px;" v-model="search.keyStr" placeholder="车辆编号/车辆型号/车辆品牌/车辆产地/生产商ID/生产商名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.vehicleStatus" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in vehicle.searchStatusList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.isBind" placeholder="请选择状态" style="width:100%;">
            <el-option v-for="o in vehicle.searchIsBindList" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <!-- 平台 -->
      <!-- <template v-if="key_user_info.userType === 'PLATFORM'">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-tickets" type="success" size="small" @click="importExcelVisible = true">导入 Excel 表格</el-button>
        </div>
      </template> -->
      <!-- 企业 -->
      <template v-if="key_user_info.userType === 'ENTERPRISE'">
        <div style="margin-right:10px;">
          <el-button icon="el-icon-edit" type="primary" size="small" @click="returnVehicleForms(key_user_info)">批量归还车辆</el-button>
        </div>
      </template>
    </div>

    <el-table :data="vehicle.list" class="vehicleHeight">
      <el-table-column prop="vehicleCode" label="编号" width="100"></el-table-column>
      <el-table-column prop="vehiclePn" label="型号" width="100"></el-table-column>
      <el-table-column prop="vehicleBrand" label="品牌" width="80"></el-table-column>
      <template v-if="key_user_info.userType === 'ENTERPRISE'">
        <el-table-column prop="vehicleMadeIn" label="车辆产地" width="100"></el-table-column>
        <el-table-column prop="mfrsName" label="生产商" width="120"></el-table-column>
      </template>
      <template v-if="key_user_info.userType === 'PLATFORM'">
        <el-table-column prop="orgName" label="所属企业" width="150"></el-table-column>
      </template>
      <el-table-column prop="loginName" label="所属用户" width="120"></el-table-column>
      <el-table-column prop="vehicleStatusText" label="状态" width="80">
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
        <el-table-column label="电池" width="160">
          <template v-if="row.vehicleStatus === 'NORMAL'" slot-scope="{row}">
            <el-button v-if="!row.batteryId" type="text" @click="showBindForm(row)">绑定</el-button>
            <el-button v-else type="text" @click="handleUnbind(row)">解绑</el-button>
            <el-button v-if="row.batteryId" icon="el-icon-search" size="mini" type="text" @click="showHoldBindBatteryForm(row)">查看电池</el-button>
          </template>
        </el-table-column>
        <el-table-column label="配件" width="180">
          <template v-if="row.vehicleStatus === 'NORMAL'" slot-scope="{row}">
            <el-button icon="el-icon-plus" size="mini" type="text" @click="showBindPartForm(row)">添加配件</el-button>
            <el-button v-if="row.partCount > 0" icon="el-icon-search" size="mini" type="text" @click="showHoldBindPartForm(row)">查看配件</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="{row}">
            <el-button icon="el-icon-edit" size="mini" type="text" @click="showEditForm(row)">详情/编辑</el-button>
            <el-button icon="el-icon-search" size="mini" type="text" @click="showVehicleLocation(row)">查看车辆位置</el-button>
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
        <el-table-column label="操作" width="200">
          <template slot-scope="{row}">
            <el-button icon="el-icon-search" size="mini" type="text" @click="showVehicleLocation(row)">查看车辆位置</el-button>
          </template>
        </el-table-column>
      </template>
    </el-table>

    <el-pagination v-if="vehicle.total" style="margin-top:10px;"
      @size-change="handleSizeChange"
      @current-change="reload"
      :current-page.sync="vehicle.currentPage"
      :page-sizes="vehicle.pageSizes"
      :page-size="vehicle.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="vehicle.total">
    </el-pagination>

    <!-- 添加车辆页面 -->
    <el-dialog title="添加车辆" :visible.sync="vehicle.formVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="form" ref="form" :rules="rules1">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="vehicleCode" label="编号">
              <el-input v-model="form.vehicleCode" auto-complete="off" :disabled="vehicle.vehicleIdForm"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehiclePn" label="型号">
              <el-input v-model="form.vehiclePn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleBrand" label="品牌">
              <el-input v-model="form.vehicleBrand" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleMadeIn" label="产地">
              <el-input v-model="form.vehicleMadeIn" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="mfrsId" label="生产商">
              <el-select v-model="form.mfrsId" placeholder="请选择生产商" style="width:100%;">
                <el-option v-for="o in mfrsList" :key="o.id" :label="o.mfrsName" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="vehicleStatus" :rules="[{required:true, message:'请选择状态'}]" label="状态">
              <el-select v-model="form.vehicleStatus" placeholder="请选择状态" style="width:100%;">
                <el-option v-for="o in statusTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="flag" label="电池信息">
              <el-select v-model="flag" placeholder="请选择状态" style="width:100%;" @change="changeBattery(flag)">
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
              <el-form-item prop="batteryName" label="电池货名">
                <el-input v-model="batteryForm.batteryName" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryBrand" label="电池品牌">
                <el-input v-model="batteryForm.batteryBrand" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryPn" label="电池型号">
                <el-input v-model="batteryForm.batteryPn" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryParameters" label="电池参数">
                <el-input v-model="batteryForm.batteryParameters" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="mfrsId" label="电池生产商">
                <el-select v-model="batteryForm.mfrsId" placeholder="请选择生产商" style="width:100%;">
                  <el-option v-for="o in mfrsList" :key="o.id" :label="o.mfrsName" :value="o.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="batteryStatus" :rules="[{required:true, message:'请选择状态'}]" label="电池状态">
                <el-select v-model="batteryForm.batteryStatus" placeholder="请选择状态" style="width:100%;">
                  <el-option v-for="o in statusTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </template>
      <template v-if="flag === '1'">
        <el-form class="edit-form" :model="bindForm" ref="bindForm">
          <el-form-item prop="id" :rules="[{required:true, message:'请选择电池'}]" label="电池">
            <el-select style="width:100%;" v-model="bindForm.id" filterable placeholder="请输入电池 电池编号、电池货名、电池品牌、电池型号、电池参数、生产商ID、生产商名" :loading="bindForm_batteryLoading">
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
    <el-dialog title="编辑车辆" :visible.sync="vehicle.editFormVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="editForm" ref="editForm">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item prop="vehicleCode" :rules="[{required:true, message:'请填写编号'}]" label="编号">
              <el-input v-model="editForm.vehicleCode" auto-complete="off" :disabled="vehicle.vehicleIdForm"></el-input>
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
                <el-option v-for="o in statusTypeList" :key="o.id" :label="o.name" :value="o.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="vehicle.editFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEditForm">保存</el-button>
      </span>
    </el-dialog>

    <!-- 绑定电池页面 -->
    <el-dialog title="绑定电池" :visible.sync="battery.bindFormVisible" :close-on-click-modal="false">
      <el-form class="edit-form" :model="bindForm" ref="bindForm">
        <el-form-item prop="batteryId" :rules="[{required:true, message:'请选择电池'}]" label="电池">
          <el-select style="width:100%;" v-model="bindForm.batteryId" filterable placeholder="请输入电池 电池编号、电池货名、电池品牌、电池型号、电池参数、生产商ID、生产商名" :loading="bindForm_batteryLoading">
            <el-option v-for="o in bindForm_batteryList" :key="o.id" :label="`${o.batteryBrand}-${o.batteryPn}`" :value="o.id">
              <span style="float: left">{{ o.batteryCode }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ o.batteryPn }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="battery.bindFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBindForm">{{form.id ? '保存' : '绑定'}}</el-button>
      </span>
    </el-dialog>
    <!-- 查看所有未绑定配件-->
    <el-dialog title="配件列表" :visible.sync="allPartsFormVisible" style="margin-top:-50px" :close-on-click-modal="false" width="80%">
      <el-form :inline="true">
        <el-form-item>
          <el-input style="width:400px;" v-model="partsSearch.keyStr" placeholder="配件编码/配件货名/配件品牌/配件型号/配件参数/生产商ID/生产商名称"></el-input>
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
      <el-pagination v-if="parts.total" style="margin-top:10px;"
        @size-change="partsHandleSizeChange"
        @current-change="partsReload"
        :current-page.sync="parts.currentPage"
        :page-sizes="parts.pageSizes"
        :page-size="parts.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="parts.total">
      </el-pagination>
      <span slot="footer" class="dialog-footer">
        <el-button @click="allPartsFormVisible = false">关闭</el-button>
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
        <el-button @click="bindPartsFormVisible = false">关闭</el-button>
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

    <!-- Excel表格数据入库 -->
    <el-dialog title="导入Excel" :visible.sync="importExcelVisible" :before-close="closeExcel" style="margin-top:-50px;" :close-on-click-modal="false" width="90%" center close-on-press-escape>
      <div class="app-container">
        <upload-excel-component @on-selected-file='selected'></upload-excel-component>
        <el-table :data="tableData" border highlight-current-row style="width: 100%;margin-top:20px;height: 350px; overflow-y: auto;">
          <el-table-column v-for='item of tableHeader' :prop="item" :label="item" :key='item'>
          </el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="commitExcel">提交</el-button>
        <el-button @click="closeExcel">关闭</el-button>
      </span>
    </el-dialog>

    <div class="vehicleClass">
      <el-dialog :title="`详细地址：${this.address}`" :visible.sync="vehicleLocationVisible" :before-close="closeVehicleLocationVisible" style="margin-top:-60px" :close-on-click-modal="false" width="90%" center close-on-press-escape>
        <div class="vehicleLocationClass" style="width:100%; height:100%">
          <baidu-map @ready="handler" id="baiduMap" style="width: 100%;height:450px" :center="center" :zoom="zoom" >
            <bm-scale anchor="BMAP_ANCHOR_TOP_RIGHT"></bm-scale>
            <bm-marker
              :icon="{url: '/static/vehicle-cur.svg', size: {width: 48, height: 48}, opts:{ imageSize: {width: 48, height: 48} } }"
              :position="markerCenter"
              :dragging="false">
            </bm-marker>
            <bm-info-window :position="PopCenter" :title="this.infoWindow.title" :show="this.infoWindow.show" :width="70" :height="60">
              <p v-text="this.infoWindow.contents"></p>
            </bm-info-window>
          </baidu-map>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="closeVehicleLocationVisible">关闭</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import _ from 'lodash';
// import BMap from 'BMap'
// import {MP} from '../util/map'
import UploadExcelComponent from '@/components/UploadExcel/index'
import {
  mapState,
} from 'vuex';
import * as validate from '../util/validate';

const partsTypeList = [
  { id: 'SEATS', name: '车座' },
  { id: 'FRAME', name: '车架' },
  { id: 'HANDLEBAR', name: '车把' },
  { id: 'BELL', name: '车铃' },
  { id: 'TYRE', name: '轮胎' },
  { id: 'PEDAL', name: '脚蹬' },
  { id: 'DASHBOARD', name: '仪表盘' },
];
const statusList = [
  { id: 'NORMAL', name: '正常' },
  { id: 'FREEZE', name: '冻结/维保' },
  { id: 'INVALID', name: '作废' },
];

const checkVehicleId = (rule, value, callback) => {
  if (!value) callback(new Error('编号不能为空'));
  else if (!validate.isvalidSinogram(value)) callback(new Error('编号不能包含汉字'));
  else callback();
};

const checkVehicleNum = (rule, value, callback) => {
  if (!value) callback(new Error('数量不能为空'));
  else if (!validate.isvalidSignlessInteger(value)) callback(new Error('请输入非负正整数'));
  else callback();
};
export default {
  components: { UploadExcelComponent },
  data() {
    return {
      center: {lng: 0, lat: 0},
      zoom: 3,
      vehiclLocation:{},
      vehiclPowner:{},
      markerCenter: { lng: 0, lat: 0 },
      PopCenter: { lng: 0, lat: 0 },
      infoWindow: {
        title: '',
        show: true,
        contents: ''
      },
      // 地址解析
      address: '',

      // Excel表格参数
      tableData: [],
      tableHeader: [],

      importExcelVisible: false,

      loading: false,
      vehicleNumTotal: undefined,
      // 车辆
      returnVehicleFormVehicle: false,
      returnVehicleForm: {
        count: 1,
      },
      rules1: {
        vehicleCode: [
          { required: true, validator: checkVehicleId },
        ],
      },
      rules2: {
        count: [
          { required: true, validator: checkVehicleNum },
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
      searchPartsStatusList: [
        { id: 'NORMAL', name: '正常' },
      ],
      partsList: [],
      holdPartsList: [],
      allPartsFormVisible: false,
      bindPartsFormVisible: false,
      bindPartsForm: {},
      unBindPartsForm: {},
      // 配件
      parts: {
        pageSizes: [10, 20, 50, 100],
        currentPage: 1,
        pageSize: 5,
        total: 0,
      },
      search: {
        vehicleStatus: '',
        isBind: '',
      },
      // 车辆
      vehicle: {
        list: [],
        pageSizes: [10, 20, 50, 100],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        formVisible: false,
        vehicleIdForm: false,
        editFormVisible: false,
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
      },
      form: {},
      battery: {
        bindFormVisible: false,
      },
      bindForm: {},
      bindForm_batteryList: [],
      bindForm_batteryLoading: false,
      // 编辑车辆
      editForm: {},

      statusTypeList: [
        { id: 'NORMAL', name: '正常' },
        { id: 'FREEZE', name: '冻结/维保' },
        { id: 'INVALID', name: '作废' },
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

      // 车辆地址
      vehicleLocationVisible: false,
    };
  },
  computed: {
    ...mapState({
      key_user_info: state => state.key_user_info,
      key_res_info: state => state.key_res_info,
      res: state => _.mapValues(_.groupBy(state.key_res_info, 'resType'), o => _.map(o, 'resCode')),
    }),
  },
  methods: {
    handler ({BMap, map}) {
      this.center.lng = this.vehiclLocation.LON;
      this.center.lat = this.vehiclLocation.LAT;
      this.PopCenter = this.center;
      this.markerCenter = this.center;
      this.zoom = 15;

      const myGeo = new BMap.Geocoder();
      const thisOne = this;
      myGeo.getLocation(new BMap.Point(thisOne.center.lng, thisOne.center.lat), function(result){
          if (result){
            thisOne.address = result.address
          }
      });
    },
    async showVehicleLocation(row) {
      try {
        // 获取坐标
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/getlocbyvehiclepk',[row.id])).body;
        if (code !== '200') throw new Error(message);
        this.vehiclLocation = respData[0];

        // 获取电池剩余电量
        const { code: pCode, message: pMess, respData: pRes } = (await this.$http.post('/api/manager/vehicle/getpowerbyvehiclepk',[row.id])).body;
        if (pCode !== '200') throw new Error(pMess);
        this.infoWindow.contents = `电池电量:  ${pRes[0].RSOC} %`;

        this.vehicleLocationVisible = true;
        this.vehicleLocationVisible = false;
        this.vehicleLocationVisible = true;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 车辆地址 回调
    closeVehicleLocationVisible() {
      this.vehicleLocationVisible = false;
    },
    // 提交 入库
    async commitExcel() {
    },
    // 关闭Excel窗口
    closeExcel() {
      this.importExcelVisible = false;
      this.tableHeader = [];
      this.tableData = [];
    },
    selected(data) {
      let results;
      this.tableHeader = data.header;
      this.tableData = results = data.results;
      // _.pluck(results, )
    },

    // 车辆列表里的 电池信息下拉框('0': 新电池 '1': 旧电池 '2': 无电池 )
    async changeBattery(flag) {
      // 旧电池  调取接口数据
      if (flag === '1') {
        await this.remoteBattery('');
      }
    },
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
          batteryStatusText: (_.find(statusList, { id: o.batteryStatus }) || { name: o.batteryStatus }).name,
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
    // 分页下拉
    async partsHandleSizeChange(partsPageSize) {
      this.parts.pageSize = partsPageSize;
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
          partsTypeText: (_.find(partsTypeList, { id: o.partsType }) || { name: o.partsType }).name,
          partsStatusText: (_.find(statusList, { id: o.partsStatus }) || { name: o.partsStatus }).name,
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
          partsTypeText: (_.find(partsTypeList, { id: o.partsType }) || { name: o.partsType }).name,
          partsStatusText: (_.find(statusList, { id: o.partsStatus }) || { name: o.partsStatus }).name,
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
    // 保存配件页面
    async savePartsForm() {
      this.$message.success('保存成功');
      this.allPartsFormVisible = false;
    },
    async handleSizeChange(vehiclePageSize) {
      this.vehicle.pageSize = vehiclePageSize;
      await this.reload();
    },
    async reload() {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/vehicle/list', {
          currPage: this.vehicle.currentPage, pageSize: this.vehicle.pageSize, ...this.search,
        })).body;
        if (code === '40106') {
          this.$store.commit('relogin');
          throw new Error('认证超时，请重新登录');
        }
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.vehicle.total = total;
        this.vehicle.list = _.map(rows, o => ({
          ...o,
          vehicleStatusText: (_.find(statusList, { id: o.vehicleStatus }) || { name: o.vehicleStatus }).name,
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
          currPage: this.parts.currentPage, pageSize: this.parts.pageSize, ...this.partsSearch, isBind: 'UNBIND',
        })).body;
        if (code !== '200') throw new Error(message);
        const { total, rows } = respData;
        this.parts.total = total;
        this.partsList = _.map(rows, o => ({
          ...o,
          partsTypeText: (_.find(partsTypeList, { id: o.partsType }) || { name: o.partsType }).name,
          partsStatusText: (_.find(statusList, { id: o.partsStatus }) || { name: o.partsStatus }).name,
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
    // 添加车辆
    async addVehicleForm(form = { }) {
      await this.getMfrs();
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
        this.vehicle.vehicleIdForm = true;
      } else {
        this.vehicle.vehicleIdForm = false;
      }
      this.vehicle.formVisible = true;
    },
    // 关闭车辆添加页面
    closeForm() {
      const $batteryForm = this.$refs.batteryForm;
      if ($batteryForm) $batteryForm.resetFields();
      const $bindForm = this.$refs.bindForm;
      if ($bindForm) $bindForm.resetFields();
      this.vehicle.formVisible = false;
    },
    // 添加车辆页面 '0': 新电池, '1': 旧电池 , '2': 无电池
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
    async showEditForm(form = {}) {
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
        await this.getMfrs();
        this.vehicle.vehicleIdForm = true;
      } else {
        this.vehicle.vehicleIdForm = false;
      }
      this.vehicle.editFormVisible = true;
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
        this.vehicle.editFormVisible = false;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async showBindForm({ id }) {
      await this.remoteBattery('');
      this.bindForm = { vehicleId: id };
      this.battery.bindFormVisible = true;
    },
    // 单独绑定电池页面
    async saveBindForm() {
      try {
        const { ...form } = this.bindForm;
        const { code, message } = (await this.$http.post('/api/manager/vehicle/batterybind', form)).body;
        if (code !== '200') throw new Error(message);
        this.$message.success('绑定成功');
        await this.reload();
        this.battery.bindFormVisible = false;
      } catch (e) {
        if (!e) return;
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    async handleUnbind({ id, batteryId }) {
      try {
        this.$confirm('确认解绑该车电池, 是否继续?', '提示', {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(async () => {
          const { code, message } = (await this.$http.post('/api/manager/vehicle/batteryunbind', { vehicleId: id, batteryId })).body;
          if (code !== '200') throw new Error(message);
          await this.reload();
          this.$message({
            type: 'success',
            message: '解绑成功',
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消解绑',
          });
        });
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 获取正常未绑定的电池集合
    async remoteBattery(keyStr) {
      try {
        const { code, message, respData } = (await this.$http.post('/api/manager/battery/list', {
          keyStr, needPaging: false, batteryStatus: 'NORMAL', isBind: 'UNBIND',
        })).body;
        if (code !== '200') throw new Error(message);
        const { rows } = respData;
        this.bindForm_batteryList = _.map(rows, o => ({
          ...o,
        }));
      } catch (e) {
        const message = e.statusText || e.message;
        this.$message.error(message);
      }
    },
    // 获取制造商集合
    async getMfrs() {
      // 平台用户权限
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
      } else {
        this.mfrsList = [];
      }
    },
  },
  async created() {
    this.loading = true;
    await this.reload();
    // await this.partsReload();
    this.loading = false;
  },
};
</script>

<style scoped>
.vehicleClass >>>.el-dialog__body   {
    padding: 15px 25px 5px;
}

.edit-form >>> .el-form-item {
  height: 55px;
}
>>> .vehicleHeight {
  position: relative;
  /* overflow-x: auto; */
  /* white-space: nowrap; */
  overflow-y: auto;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  width: 100%;
  /* max-width: 100%; */
  height: 85%;
  max-height: 85%;
  color: #606266;
}
</style>
